package com.example.demo.service;

import com.example.demo.model.Spittle;

/**
 * @author ChangLiang
 * @date 2018/5/28
 */
public interface RabbitmqService {

    void sendSimpleQueue(Spittle spittle);

    void sendTopicQueue(Spittle spittle);
}
