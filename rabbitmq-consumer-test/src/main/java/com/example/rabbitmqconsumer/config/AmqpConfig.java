package com.example.rabbitmqconsumer.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ChangLiang
 * @date 2018/5/29
 */
@Configuration
public class AmqpConfig {

    @Value("${rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.port}")
    private String port;

    @Value("${rabbitmq.username}")
    private String username;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.vhost}")
    private String vhost;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(host+":"+port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);
        return connectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean(name = "autoAckContainerFactory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> AutoAckContainerFactory(ConnectionFactory
                                                                                               connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        //设置自动签收消息
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }

    /// 参数为Message时使用

/*    @Bean(name = "manualAckContainerFactory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> ManualAckContainerFactory(ConnectionFactory
                                                                                                          connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }*/
    /*
    * 工程启动时创建bean，以@Bean的形式替代使用配置文件.xml的形式
    * */
    @Bean(name = "manualAckContainerFactory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> ManualAckContainerFactory(ConnectionFactory
                                                                                                            connectionFactory) {
        /**
         * spring容器中构建SimpleMessageListenerContainer来消费消息，我们也可以使用@RabbitListener来消费消息
         * //SimpleRabbitListenerContainerFactory发现消息中有content_type有text就会默认将其转换成string类型的
         */
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(jsonMessageConverter());
        //设置为手动签收消息
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);

        /**如果生产端发送的list或者map类型的数据，可以使用MessageListenerAdapter
         * MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageHandler());
         //指定Json转换器
         adapter.setMessageConverter(new Jackson2JsonMessageConverter());
         //设置处理器的消费消息的默认方法
         adapter.setDefaultListenerMethod("onMessage");在onMessage方法中对消息进行处理
         container.setMessageListener(adapter);
         */
        return factory;
    }
}
