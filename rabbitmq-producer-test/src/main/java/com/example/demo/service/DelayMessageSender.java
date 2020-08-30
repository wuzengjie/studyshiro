package com.example.demo.service;

import com.example.demo.model.DelayTypeEnum;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.example.demo.config.RabbitMQConfig.DELAY_EXCHANGE_NAME;
import static com.example.demo.config.RabbitMQConfig.DELAY_QUEUEA_ROUTING_KEY;
import static com.example.demo.config.RabbitMQConfig.DELAY_QUEUEB_ROUTING_KEY;

@Component
public class DelayMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMsg(String msg, String s){
        switch(2){
            case 1:
                rabbitTemplate.convertAndSend(DELAY_EXCHANGE_NAME, DELAY_QUEUEA_ROUTING_KEY, msg);
                break;
            case 2:
                rabbitTemplate.convertAndSend(DELAY_EXCHANGE_NAME, DELAY_QUEUEB_ROUTING_KEY, msg);
                break;
        }
    }
}