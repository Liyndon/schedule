package com.schedule.core.service.listener;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by fengwei.cfw on 2017/9/13.
 */
@Configuration
public class MqConf {

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("hxc.topic.exchange");
    }

    @Bean
    public Queue registerQueue() {
        return new Queue("schedule.job.register");
    }

    @Bean
    public Queue callbackQueue() {
        return new Queue("schedule.job.callback");
    }

    @Bean
    Binding bindingRegisterQueue(Queue registerQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(registerQueue).to(topicExchange).with(registerQueue.getName());
    }

    @Bean
    Binding bindingCallbackQueue(Queue callbackQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(callbackQueue).to(topicExchange).with(callbackQueue.getName());
    }
}
