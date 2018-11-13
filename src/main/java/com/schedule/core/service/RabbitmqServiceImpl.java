package com.schedule.core.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by fengwei.cfw on 2017/9/13.
 */
@Service
public class RabbitmqServiceImpl implements RabbitmqService {

    @Autowired
    private AmqpTemplate template;

    @Override
    public void send(String queue, Object message) {
        template.convertAndSend(queue, message);
    }
}
