package com.example.demo.controller;


import com.example.demo.model.Spittle;
import com.example.demo.service.RabbitmqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ChangLiang
 * @date 2018/5/28
 */
@RestController
public class RabbitmqController {

    @Autowired
    private RabbitmqService rabbitmqService;

    @GetMapping("/helloSimpleQueue")
    public Spittle sayHelloSimpleQueue(){
        //调用service
        Spittle spittle = new Spittle(1,"Hello world");
        rabbitmqService.sendSimpleQueue(spittle);
        return spittle;
    }

    @GetMapping("/helloTopicQueue")
    public Spittle sayHelloTopicQueue(){
        //调用service
        Spittle spittle = new Spittle(1,"Hello world");
        rabbitmqService.sendTopicQueue(spittle);
        return spittle;
    }



/*    @Autowired
    private RabbitSender rabbitSender;

    @GetMapping("/helloRabbitmq")
    public Spittle sayHello(){
        Map<String, Object> properties = new HashMap<>(2);
        properties.put("number", "12345");
        properties.put("send_time", System.currentTimeMillis());
        //调用service
        Spittle spittle = new Spittle(1,"Hello world");
        rabbitSender.send(spittle,properties);
        return spittle;
    }*/
}
