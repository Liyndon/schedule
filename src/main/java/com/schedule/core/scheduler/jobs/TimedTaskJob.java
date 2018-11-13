package com.schedule.core.scheduler.jobs;

import com.schedule.core.service.SchedulerService;
import com.schedule.dao.domain.CommonResult;
import com.schedule.dao.domain.ScheduleJob;
import com.schedule.util.ContextLocator;
import com.schedule.util.LoggerUtil;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by fengwei.cfw on 2017/4/27.
 */
@Component
public class TimedTaskJob implements Job {

    private Logger LOGGER = LoggerFactory.getLogger(TimedTaskJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        Integer id = null;
        try {
            id = Integer.valueOf(jobDetail.getKey().getName());
        } catch (Exception e) {
            LoggerUtil.error(LOGGER, "检查到非法定时编号:{}", jobDetail.getKey().getName());
        }
        LoggerUtil.info(LOGGER, "execute job,name:{},group:{}", jobDetail.getKey().getName(), jobDetail.getKey().getGroup());
        SchedulerService schedulerService = ContextLocator.getBean(SchedulerService.class);
        CommonResult jobResult = schedulerService.get(id);
        if (jobResult.getObject() == null) {
            LoggerUtil.error(LOGGER, "not find job,name:{}", jobDetail.getKey().getName());
            return;
        }
        ScheduleJob job = (ScheduleJob) jobResult.getObject();
        schedulerService.execute(job);
    }
}
