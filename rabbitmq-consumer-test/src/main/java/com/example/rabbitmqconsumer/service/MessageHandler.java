package com.example.rabbitmqconsumer.service;

import com.example.rabbitmqconsumer.model.Spittle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class MessageHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${rabbitmq.topic.queue}", durable = "true"),
                    exchange = @Exchange(value = "${rabbitmq.topic.exchange}", type = "topic",
                            ignoreDeclarationExceptions = "true",delayed="true"),
                    key = "${rabbitmq.topic.routingKey}"
            )
    )
    @RabbitHandler
    public void onTopicQueueMessage(Message message,Channel channel) throws IOException {
        try {
            ObjectMapper mapper=new ObjectMapper();
            String messaged=new String(message.getBody());
            MessageProperties properties=message.getMessageProperties();
            long tag=properties.getDeliveryTag();
            Spittle spittle=mapper.readValue(StringEscapeUtils.unescapeJava(messaged.substring(1,messaged.length()-1)),Spittle.class);
            //处理业务，业务正常则进行签收
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("消息接收时间:"+sdf.format(new Date()));
            System.out.println(spittle);
            channel.basicAck(tag,false);
        } catch (IOException e) {
            //e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
    }


}
