package com.example.rabbitmqconsumer.service;

/*import com.example.rabbitmqconsumer.model.Spittle;*/
import com.alibaba.fastjson.JSONObject;
import com.example.rabbitmqconsumer.model.Spittle;
import com.example.rabbitmqconsumer.util.JsonConvertUtils;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


/**
 * @author ChangLiang
 * @date 2018/5/29
 */
@Service
public class MessageHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    //由于生产者使用了jackson自己来发送jsonString
    //导致客户端使用fastjson解析困难
    /*@RabbitListener(queues = "${rabbitmq.queue}", containerFactory = "jsaFactory")
    public void recievedMessage(byte[] jsonString) {
        String s = new String(jsonString);
        String substring = s.substring(1, s.length() - 1).replace("\\","");
        //String substring = "{\"id\":1,\"message\":\"Hello world\"}";
        System.out.println(substring);
        Spittle spittle = JSON.parseObject(substring, Spittle.class);

        System.out.println("Recieved Message: " + spittle.getMessage());
    }*/

    /*    public static void main(String[] args) {
        String s = "{\"id\":1,\"message\":\"Hello world\"}";
        Spittle spittle = JSON.parseObject(s, Spittle.class);
        System.out.println(spittle.getMessage());
    }*/

    //配置了messageConverter之后，mq中的数据是{"id":1,"message":"Hello world"}
    //可以直接在客户端解析，非常方便
    @RabbitListener(
            queues = "${rabbitmq.simple.queue}",
            containerFactory = "autoAckContainerFactory"
    )
    public void onSimpleQueueMessage(Spittle spittle) {

        LOGGER.info("Recieved Message: {}",spittle);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${rabbitmq.topic.queue}", durable = "true"),
                    exchange = @Exchange(value = "${rabbitmq.topic.exchange}", type = "topic",
                            ignoreDeclarationExceptions = "true"),
                    key = "${rabbitmq.topic.routingKey}"
            ),
            containerFactory = "manualAckContainerFactory"
    )
    @RabbitHandler
    public void onTopicQueueMessage(
            @Payload JSONObject object,
            @Headers Map<String,Object> headers,
            Channel channel
    ) {
        LOGGER.info("payload: {}", JsonConvertUtils.convertJSONToObject(object));
        LOGGER.info("headers: {}", headers);
        // 手工ack
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        LOGGER.info("deliveryTag: {}",deliveryTag);
       try {
            channel.basicAck(deliveryTag,false);//确认签收
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
