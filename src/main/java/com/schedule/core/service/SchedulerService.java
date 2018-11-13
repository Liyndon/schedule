package com.schedule.core.service;

import com.schedule.dao.domain.CommonResult;
import com.schedule.dao.domain.ScheduleJob;
import org.quartz.SchedulerException;

/**
 * Created by fengwei.cfw on 2017/4/25.
 */
public interface SchedulerService {

    CommonResult create(ScheduleJob job) throws SchedulerException;

    CommonResult delete(String taskId, String submitGroup) throws SchedulerException;

    CommonResult get(String taskId, String submitGroup);

    CommonResult get(Integer id);

    CommonResult getUndoneTriggerJob(Integer delaySecond);

    void execute(ScheduleJob job);

    CommonResult deleteJob();
}