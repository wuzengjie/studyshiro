package com.example.demo.service;

import com.example.demo.model.Spittle;
import com.example.demo.util.FastJsonConvertUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author ChangLiang
 * @date 2018/5/28
 */
@Service
public class RabbitmqServiceImpl implements RabbitmqService, RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    private static Logger LOGGER = LoggerFactory.getLogger(RabbitmqServiceImpl.class);


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate2;


    @PostConstruct
    public void init(){
        //rabbitTemplate如果为单例的话，那回调就是最后设置的内容
        //ConfirmCallback接口用于实现消息发送到RabbitMQ交换器后接收ack回调
        //开启rabbitmq的confirm机制,如果消息没有到达exchange,或者exchange在ack生产者的时候，生产者没有收到,那么生产者会进行重发
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }


    @Override
    public void sendTopicQueue(Spittle spittle) {

        Map<String, Object> properties = new HashMap<>(2);
        properties.put("number", "12345");
        properties.put("send_time", System.currentTimeMillis());
        MessageHeaders messageHeaders = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage(FastJsonConvertUtil.toJsonObject(spittle), messageHeaders);

        rabbitTemplate.convertAndSend("exchange-1","springboot.hello",msg);
        LOGGER.info("消息发送成功: {}", msg);
    }

    @Override
    public void sendTopicQueue2(Spittle spittle) {

        ObjectMapper mapper=new ObjectMapper();
        String messaged="";
        try {
            messaged=mapper.writeValueAsString(spittle);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("生产者："+messaged);
        CorrelationData correlationData=new CorrelationData(1+"");

        rabbitTemplate.convertAndSend("exchange-1","springboot.hello",messaged, correlationData);

    }

    @Override
    public void sendDelayTopicQueue2(Spittle spittle) {
        ObjectMapper mapper=new ObjectMapper();
        String messaged="";
        try {
            messaged=mapper.writeValueAsString(spittle);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("消息发送时间:"+sdf.format(new Date()));
        rabbitTemplate.convertAndSend("relay-exchange", "relay.aa", messaged, message -> {
            message.getMessageProperties().setDelay(6000);
            message.getMessageProperties().setCorrelationId(1+"");
            return message;
        });
    }

    //消息确认机制，如果消息已经发出，但是rabbitmq并没有回应或者是拒绝接收消息了呢？就可以通过回调函数的方式将原因打印出来
    @Override
    public void confirm(@Nullable CorrelationData correlationData, boolean ack, @Nullable String cause) {
        if (null != cause && !"".equals(cause)) {
            System.out.println("失败原因:" + cause);
            // 重发的时候到redis里面取,消费成功了，删除redis里面的msgId
            ObjectMapper mapper=new ObjectMapper();
            String messaged="";
            try {
                messaged=mapper.writeValueAsString(new Spittle());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            //rabbitTemplate.convertAndSend("exchange-m","springboot.hello",messaged, correlationData);
        }

        System.out.println("投递消息成功"+ack);
    }

    //有关消息被退回来的具体描述消息
    @Override
    public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText, String exchange, String routingKey) {
        LOGGER.error("returnedMessage [消息从交换机到队列失败]  message："+message);
        //关于routekey不匹配异常
        // rabbitTemplate.setMandatory(true);如果设置了mandatory=true(默认为false)
        // 这样设置的话，如果消息到达exchange后，没有queue与其绑定，会将消息返给生产者，生产者会
        // 回调这个方法
    }
}