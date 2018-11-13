package com.schedule.core.service;

import com.schedule.core.scheduler.SchedulerFactoryUtil;
import com.schedule.core.scheduler.jobs.TimedTaskJob;
import com.schedule.dao.ScheduleJobDao;
import com.schedule.dao.domain.CommonResult;
import com.schedule.dao.domain.ScheduleJob;
import com.schedule.dao.domain.ScheduleJobHistory;
import com.schedule.util.*;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by fengwei.cfw on 2017/4/25.
 */
@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private RabbitmqService rabbitmqService;

    private final static Logger LOGGER = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public CommonResult create(ScheduleJob job) throws SchedulerException {
        Precondition.checkNotNull(job, "job不能为空");
        Precondition.checkArgument(StringUtils.length(job.getTaskId()) > 0 && StringUtils.length(job.getTaskId()) <= 32,
                "taskId参数不合法，长度限制为(0,32]");
        Precondition.checkArgument(job.getMaxRetryTimes() >= 1 && job.getMaxRetryTimes() < 10,
                "maxRetryTimes参数不合法，大小限制为[1,10)");
        Precondition.checkArgument(StringUtils.length(job.getSubmitGroup()) > 0 && StringUtils.length(job.getSubmitGroup()) <= 36,
                "submitGroup参数不合法，长度限制为(0,36]");
        Precondition.checkArgument(StringUtils.length(job.getTrackerGroup()) > 0 && StringUtils.length(job.getTrackerGroup()) <= 128,
                "trackerGroup参数不合法，长度限制为(0,128]");
        Precondition.checkArgument(StringUtils.isNotBlank(job.getCronExpression()) || job.getGmtTrigger() != null,
                "cronExpression、gmtTrigger不能同时为空");
        Precondition.checkArgument(StringUtils.isBlank(job.getCronExpression()) || StringUtils.length(job.getCronExpression()) <= 20,
                "cronExpression参数不合法，长度限制为(0,20]");
        Precondition.checkArgument(job.getGmtTrigger() == null || job.getGmtTrigger() == 0
                || job.getGmtTrigger() > System.currentTimeMillis(), "gmtTrigger参数不合法，必须在当前时间之后");
        Precondition.checkNotNull(job.getExtParams(), "ExtParams不能为空");

        LoggerUtil.info(LOGGER, "收到schedule任务:{}", job);
        long acceptTime = System.currentTimeMillis();
        ScheduleJob exitsJob = scheduleJobDao.get(job.getTaskId(), job.getSubmitGroup());
        if (exitsJob != null) {
            LoggerUtil.info(LOGGER, "schedule任务已存在:{}", job);
            return CommonResult.fail("任务已存在");
        }
        scheduleJobDao.insert(job);

        //新增定时任务
        Scheduler scheduler = SchedulerFactoryUtil.getRandomScheduler();
        scheduler.start();

        //具体任务
        JobDetail jobDetail = JobBuilder.newJob(TimedTaskJob.class).withIdentity(job.getId().toString(), "timed_task").build();

        //触发时间点
        String cron = StringUtils.isNotBlank(job.getCronExpression()) ? job.getCronExpression() : CronUtil.getCron(job.getGmtTrigger());
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(job.getId().toString(), "timed_task")
                .withSchedule(cronScheduleBuilder).build();

        //交由Scheduler安排触发
        scheduler.scheduleJob(jobDetail, trigger);
        long finishTime = System.currentTimeMillis();
        LoggerUtil.info(LOGGER, "schedule任务新增完成:{},任务总耗时:{},timeout:{}",
                job, finishTime - acceptTime, ((finishTime - acceptTime) >= 1000));
        return CommonResult.success();
    }

    @Override
    public CommonResult delete(String taskId, String submitGroup) throws SchedulerException {
        Precondition.checkNotNull(taskId);
        Precondition.checkNotNull(submitGroup);

        ScheduleJob job = scheduleJobDao.get(taskId, submitGroup);
        if (job == null) {
            return CommonResult.fail("任务信息不存在");
        }
        scheduleJobDao.delete(job.getTaskId());
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        JobKey jobKey = new JobKey(job.getId().toString(), "timed_task");
        JobDetail detail = scheduler.getJobDetail(jobKey);
        if (detail != null) {
            scheduler.deleteJob(jobKey);
        }
        return CommonResult.success();
    }

    @Override
    public CommonResult get(String taskId, String submitGroup) {
        Precondition.checkNotNull(taskId);
        Precondition.checkNotNull(submitGroup);

        ScheduleJob job = scheduleJobDao.get(taskId, submitGroup);
        return CommonResult.success(job);
    }

    @Override
    public CommonResult get(Integer id) {
        Precondition.checkNotNull(id);

        ScheduleJob job = scheduleJobDao.get(id);
        return CommonResult.success(job);
    }

    @Override
    public CommonResult getUndoneTriggerJob(Integer delaySecond) {

        List<ScheduleJob> scheduleJobs = scheduleJobDao.getUndoneTriggerJob(delaySecond);
        return CommonResult.success(scheduleJobs);
    }

    @Override
    public void execute(ScheduleJob job) {
        Precondition.checkNotNull(job);
        CommonTreadPool.getInstance("").execute(new Runnable() {
            @Override
            public void run() {
                long beginTime = System.currentTimeMillis();
                ScheduleJobHistory history = new ScheduleJobHistory();
                history.setTaskId(job.getTaskId());
                try {
                    rabbitmqService.send("schedule.job.callback", job.getExtParams());
                } catch (AmqpException e) {
                    history.setStatus("fail");
                    history.setResult("mq投递失败");
                    LoggerUtil.error(LOGGER, e, "任务执行失败,job:{}", job);
                    return;
                } finally {
                    history.setStatus("success");
                    history.setResult("mq投递成功");
                    history.setElapsedTime(System.currentTimeMillis() - beginTime);
                    scheduleJobDao.insertHistory(history);
                    job.setRetryTimes(job.getRetryTimes() + 1);
                    scheduleJobDao.update(job);
                    LoggerUtil.info(LOGGER, "任务执行成功:{}", job);
                }
            }
        });
    }

    @Override
    public CommonResult deleteJob() {
        Integer num = scheduleJobDao.deleteJob();
        return CommonResult.success(num);
    }
}