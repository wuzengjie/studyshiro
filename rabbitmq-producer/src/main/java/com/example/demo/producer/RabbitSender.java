package com.example.demo.producer;

import org.springframework.stereotype.Component;

/**
 * @author ChangLiang
 * @date 2019/1/29
 */
@Component
public class RabbitSender {

/*    private static Logger LOGGER = LoggerFactory.getLogger(RabbitSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            LOGGER.info("correlationData: {}", correlationData);
            LOGGER.info("ack: {}", ack);
            if (!ack) {
                LOGGER.info("cause: {}", cause);
            }
        }
    };

    final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText,
                                    String exchange, String routingKey) {
            LOGGER.info("return message: {}, replyCode: {}, replyText:{}, exchange: {}, routingKey: {}",
                    message,replyCode,replyText,exchange,routingKey);
        }
    };

    public void send(Object message, Map<String, Object> properties) {
        MessageHeaders messageHeaders = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage(message, messageHeaders);
        /// Only one ConfirmCallback is supported by each RabbitTemplate
        // 支持消息confirm模式
        rabbitTemplate.setConfirmCallback(confirmCallback);

        // 支持消息return模式
        rabbitTemplate.setReturnCallback(returnCallback);
        rabbitTemplate.convertAndSend("exchange-1","springboot.hello",msg);
    }*/
}
