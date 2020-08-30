package com.example.demo.service;

import com.example.demo.model.Spittle;

/**
 * @author ChangLiang
 * @date 2018/5/28
 */
public interface RabbitmqService {

    void sendTopicQueue(Spittle spittle);
    void sendTopicQueue2(Spittle spittle);
    void sendDelayTopicQueue2(Spittle spittle);
}
