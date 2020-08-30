package com.study.util;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 进行连接redis数据库
 */
@Component
public class JedisUtil {

    @Resource
    private JedisPool jedisPool;

    private Jedis getResource(){
        return jedisPool.getResource();
    }
    public byte[] set(byte[] key, byte[] value) {
        Jedis jedis = getResource();
        try {
            jedis.set(key, value);
            return value;
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return "发生异常".getBytes();
    }

    public void expire(byte[] key,int i) {
        Jedis jedis = getResource();
        try {
            jedis.expire(key,i);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
    }

    public byte[] get(byte[] key) {
        Jedis jedis=getResource();
        try{
            byte[] value=jedis.get(key);
            return value;
        }finally {
            jedis.close();
        }

    }

    public void del(byte[] key) {
        Jedis jedis=getResource();
        try{
          jedis.del(key);
        }finally {
            jedis.close();
        }
    }

    public Set<byte[]> keys(String shiro_session_prefix) {
        Jedis jedis=getResource();
        try{
            return jedis.keys((shiro_session_prefix+"*").getBytes());
        }finally {
            jedis.close();
        }
    }

    /**
     * 存储Redis队列，顺序存储
     * @param key redis键名
     * @param value 键值
     */
    public void lpushRedisList(String key, String value) {
        Jedis jedis = getResource();
        try {
            jedis.lpush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 移除并获取列表最后一个元素
     * @param key
     * @return
     */
    public String rpopRedisList(String key) {
        Jedis jedis = getResource();
        try {
            return jedis.rpop(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

}
