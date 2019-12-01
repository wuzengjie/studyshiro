package com.example.demo.service;

import com.example.demo.model.Spittle;
import com.example.demo.util.FastJsonConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * @author ChangLiang
 * @date 2018/5/28
 */
@Service
public class RabbitmqServiceImpl implements RabbitmqService, RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    private static Logger LOGGER = LoggerFactory.getLogger(RabbitmqServiceImpl.class);

    private static final String QUEUE_NAME="test_simple_queue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate2;

    @Autowired
    private RabbitAdmin amqpAdmin;

    @PostConstruct
    public void init(){
        //rabbitTemplate如果为单例的话，那回调就是最后设置的内容
        //ConfirmCallback接口用于实现消息发送到RabbitMQ交换器后接收ack回调
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public void sendSimpleQueue(Spittle spittle) {
        try {
            amqpAdmin.declareQueue(new Queue(QUEUE_NAME,true));

            /// 这里使用了jackson自己来发送jsonString
            //这会导致mq中的数据是这种类型 {\"id\":1,\"message\":\"Hello world\"}
            //导致客户端解析困难
/*            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(spittle);
            rabbitTemplate.convertAndSend(QUEUE_NAME, jsonString);
            System.out.println("消息发送成功: "+jsonString);*/



            /// 配置了messageConverter之后，mq中的数据是{"id":1,"message":"Hello world"}
            // 可以直接在客户端解析，非常方便

            // 一般是要根据业务 要全局唯一的 以后做deliveryTag 根据这个id 找到唯一一条消息
            CorrelationData correlationData = new CorrelationData((UUID.randomUUID().toString()));
            rabbitTemplate.convertAndSend(QUEUE_NAME, spittle,correlationData);
            LOGGER.info("消息发送成功: {}", spittle);

        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendTopicQueue(Spittle spittle) {
        Map<String, Object> properties = new HashMap<>(2);
        properties.put("number", "12345");
        properties.put("send_time", System.currentTimeMillis());
        MessageHeaders messageHeaders = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage(FastJsonConvertUtil.toJsonObject(spittle), messageHeaders);

        // 支持消息return模式
        rabbitTemplate2.convertAndSend("exchange-1","springboot.hello",msg);
        LOGGER.info("消息发送成功: {}", msg);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
/*        System.out.println(" 回调id:" + correlationData);
        if (ack) {
            System.out.println("消息成功发送到rabbitmq成功");
        } else {
            System.out.println("消息发送到rabbitmq失败:" + cause);
        }*/
        LOGGER.info("客户端成功收到消息");
        LOGGER.info("correlationData: {}", correlationData);
        LOGGER.info("ack: {}", ack);
        if (!ack) {
            LOGGER.info("cause: {}", cause);
        }
    }


    @Override
    public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText,
                                String exchange, String routingKey) {
        LOGGER.error("return message: {}",message);
        LOGGER.error("replyCode: {}",replyCode);
        LOGGER.error("replyText:{}",replyText);
        LOGGER.error("exchange: {}",exchange);
        LOGGER.error("routingKey: {}",routingKey);
    }
}