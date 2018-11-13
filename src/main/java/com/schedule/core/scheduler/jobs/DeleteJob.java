package com.schedule.core.scheduler.jobs;

import com.schedule.core.service.SchedulerService;
import com.schedule.dao.domain.CommonResult;
import com.schedule.util.ContextLocator;
import com.schedule.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by fengwei.cfw on 2017/10/10.
 */
@Component
public class DeleteJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(OffsetJob.class);

    @Scheduled(cron = "0/30 * * * * ?")
    public synchronized void offset() {
        SchedulerService schedulerService = ContextLocator.getBean(SchedulerService.class);
        CommonResult jobResult = schedulerService.deleteJob();
        Integer num = (Integer) jobResult.getObject();
        LoggerUtil.info(LOGGER, "删除已过期任务:{}个", num);
    }
}
