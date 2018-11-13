package com.schedule.controller;

import com.schedule.core.service.SchedulerService;
import com.schedule.dao.domain.CommonResult;
import com.schedule.dao.domain.ScheduleJob;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by fengwei.cfw on 2017/4/26.
 */
@RestController
public class ScheduleController {

    @Autowired
    private SchedulerService schedulerService;

    @RequestMapping(value = "/schedule", method = RequestMethod.POST)
    public CommonResult add(@RequestBody ScheduleJob job) throws SchedulerException {
        return schedulerService.create(job);
    }

    @RequestMapping(value = "/schedule", method = RequestMethod.DELETE)
    public CommonResult delete(String taskId, String submitGroup) throws SchedulerException {
        return schedulerService.delete(taskId, submitGroup);
    }

    @RequestMapping(value = "/schedule", method = RequestMethod.GET)
    public CommonResult get(String taskId, String submitGroup) {
        return schedulerService.get(taskId, submitGroup);
    }
}
