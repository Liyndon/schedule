package com.schedule.core.scheduler.jobs;

import com.schedule.core.service.SchedulerService;
import com.schedule.dao.domain.CommonResult;
import com.schedule.dao.domain.ScheduleJob;
import com.schedule.util.ContextLocator;
import com.schedule.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by fengwei.cfw on 2017/8/18.
 */
@Component
public class OffsetJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(OffsetJob.class);

    @Scheduled(cron = "0/10 * * * * ?")
    public void offset() {
        SchedulerService schedulerService = ContextLocator.getBean(SchedulerService.class);
        CommonResult jobResult = schedulerService.getUndoneTriggerJob(10);
        List<ScheduleJob> scheduleJobs = (List<ScheduleJob>) jobResult.getObject();
        if (CollectionUtils.isEmpty(scheduleJobs)) {
            return;
        }

        LoggerUtil.info(LOGGER, "捞取到补偿任务:{}个", scheduleJobs.size());
        for (ScheduleJob job : scheduleJobs) {
            schedulerService.execute(job);
        }
    }
}
