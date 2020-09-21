package com.example.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /*添加list集合*/

    public Long setList(String key,List<String> value){
        return stringRedisTemplate.opsForList().rightPushAll(key,value);/*查看返回值ctrl+q*/
    }

    public String getFirstVal(String key){
        return stringRedisTemplate.opsForList().index(key,stringRedisTemplate.opsForList().size(key)-1);
    }

    public String popLeftVal(String key){
        return stringRedisTemplate.opsForList().leftPop(key);
    }
}
