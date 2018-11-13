package com.schedule.controller;

import com.google.gson.Gson;
import com.schedule.core.service.SchedulerService;
import com.schedule.dao.domain.ScheduleJob;
import com.schedule.util.LoggerUtil;
import com.schedule.util.PreconditonException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: fengwei.cfw
 * @Description:
 * @Date: Created in 2018/7/4 上午9:24
 */
@Component
public class ScheduleRegisterListener {

    @Autowired
    private SchedulerService schedulerService;

    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleRegisterListener.class);

    @RabbitListener(queues = "schedule.job.register")
    public void registerJob(Message message) {
        ScheduleJob job = null;
        try {
            String messageStr = new String(message.getBody());
            LoggerUtil.info(LOGGER, "Received message[schedule_job]:{}", messageStr);
            Gson gson = new Gson();
            ScheduleJob job2 = gson.fromJson(messageStr, ScheduleJob.class);
            schedulerService.create(job2);
        } catch (SchedulerException e) {
            LoggerUtil.error(LOGGER, e, "创建任务时，schedule异常:{}", job);
        } catch (PreconditonException e) {
            LoggerUtil.error(LOGGER, e, "创建任务时，参数校验异常:{}", job);
        } catch (Exception e) {
            LoggerUtil.error(LOGGER, e, "创建任务时，未知异常:{}", job);
        }
    }
}
