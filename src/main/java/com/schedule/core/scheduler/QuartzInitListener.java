package com.schedule.core.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by fengwei.cfw on 16/10/11.
 */
@Component
public class QuartzInitListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        try {
            List<Scheduler> schedulers = SchedulerFactoryUtil.getSchedulers();
            for (Scheduler scheduler : schedulers) {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
