package com.example.redis.controller;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DemoJobHandler {

    @XxlJob("demoJobHandler")
    public ReturnT<String> execute(String param) {
        System.out.println("执行了");
        XxlJobLogger.log("hello world.");
        return ReturnT.SUCCESS;
    }
}
