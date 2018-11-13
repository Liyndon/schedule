package com.schedule.core.service;

/**
 * Created by fengwei.cfw on 2017/9/13.
 */
public interface RabbitmqService {

    void send(String queue, Object message);
}
