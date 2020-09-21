package com.example.redis.controller;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
public class RedisLockController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**因为redis是单线程的所以无论有多少个线程同时到达redis,最终都会以串行的方式执行*/
    /**此种做法只能是单机，而且如果网络出现抖动，前一部分执行时间过长，导致锁过期释放，还是会出现问题*/
    /**可以采用以机制，加完锁或，开启一条异步线程，不断给当前还处于执行中的线程，所获得的锁进行续期*/
    @ResponseBody
    @RequestMapping("/")
    public String addRedisLock(){

        String uuid= IdUtil.randomUUID();
       //使用setnx进行加锁，因为setnx保证值不存在的话写入返回true,如果存在直接返回false,但是不能保证与过期时间的设置为原子性操作
        // ,所以后期使用setIfAbsent
        /*10s进行解锁，防止会产生死锁*/
        boolean result=stringRedisTemplate.opsForValue().setIfAbsent("Lock",uuid,10, TimeUnit.SECONDS);
        if(!result){
            return "error";
        }
        try{
            int num=Integer.valueOf(stringRedisTemplate.opsForValue().get("stock"));
            System.out.println("剩余库存："+num);
            /*模拟减库存的做法*/
            if(num>0){
                num=num-1;//并发环境下会出现重复减库存的情况
                stringRedisTemplate.opsForValue().set("stock",num+"");
            }
            /*放在finally中是为了防止程序执行异常，锁得不到释放*/
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            /*如果直接执行删除，不能保证某条线程加的锁，就由某条线程解锁*/
            //stringRedisTemplate.delete("Lock");
            /*此种做法依然有问题，因为在多线程的环境下不能保证下面的操作为原子性的操作*/
            /*if(uuid.equals(stringRedisTemplate.opsForValue().get("Lock"))){
                stringRedisTemplate.delete("Lock");
            }*/
            /*所以此时还需要使用lua脚本保证获取uuid进行判断与删除锁是一个原子操作*/
            String RELEASE_LOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            // 指定 lua 脚本，并且指定返回值类型
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(RELEASE_LOCK_LUA_SCRIPT,Long.class);
            // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
            Long res = stringRedisTemplate.execute(redisScript, Collections.singletonList("Lock"),uuid);
            if(res==1){
                System.out.println("释放锁");
            }
        }
        return "";
    }

}


