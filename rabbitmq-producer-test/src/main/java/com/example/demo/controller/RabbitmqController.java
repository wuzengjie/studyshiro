package com.example.demo.controller;


import com.example.demo.model.DelayTypeEnum;
import com.example.demo.model.Spittle;
import com.example.demo.service.DelayMessageSender;
import com.example.demo.service.RabbitmqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author ChangLiang
 * @date 2018/5/28
 */
@RestController
public class RabbitmqController {

    @Autowired
    private RabbitmqService rabbitmqService;

    @GetMapping("/helloTopicQueue")
    public Spittle sayHelloTopicQueue(){
        //调用service
        Spittle spittle = new Spittle(1,"Hello world");
        rabbitmqService.sendDelayTopicQueue2(spittle);
        return spittle;
    }

    @Autowired
    private DelayMessageSender delayMessageSender;

    @GetMapping("/delayQueue")
    public Spittle delayQueue(){
        //调用service
        Spittle spittle = new Spittle(1,"Hello world");

        delayMessageSender.sendMsg("aaa", "1");
        return spittle;
    }

    @ResponseBody
    @RequestMapping("test")
    @Scope
    public String test1() throws InterruptedException {
        System.out.println("1");
        Thread.sleep(10000000);
        return "aaaaaaaaaaaaaaa";
    }
}
