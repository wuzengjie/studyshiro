package com.study.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import java.util.Collections;
import java.util.UUID;
/**学习连接 http://www.zzvips.com/article/49628.html*/
/**
 * 命令必须保证互斥
 * 设置的key必须要有过期时间，防止崩溃时锁无法释放
 * value使用唯一id标志每个客户端，保证只有锁的持有者才可以释放锁
 * 加锁直接使用set命令同时设置唯一id和过期时间；其中解锁稍微复杂些，
 * 加锁之后可以返回唯一id，标志此锁是该客户端锁拥有；释放锁时要先判
 * 断拥有者是否是自己，然后删除，这个需要redis的lua脚本保证两个命
 * 令的原子性执行。
 */
public class RedisDistributedLock {
    private static Logger log= LoggerFactory.getLogger(RedisDistributedLock.class);
    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    // 锁的超时时间
    private static int EXPIRE_TIME = 5 * 1000;
    // 锁等待时间
    private static int WAIT_TIME = 1 * 1000;

    private Jedis jedis;
    private String key;

    public RedisDistributedLock(Jedis jedis, String key) {
        this.jedis = jedis;
        this.key = key;
    }
    // 不断尝试加锁
    public String lock() {
        try {
            // 超过等待时间，加锁失败
            long waitEnd = System.currentTimeMillis() + WAIT_TIME;
            String value = UUID.randomUUID().toString();
            while (System.currentTimeMillis() < waitEnd) {
                String result = jedis.set(key, value, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, EXPIRE_TIME);
                if (LOCK_SUCCESS.equals(result)) {
                    return value;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception ex) {
            log.error("lock error", ex);
        }
        return null;
    }

    public boolean release(String value) {
        if (value == null) {
            return false;
        }
        // 判断key存在并且删除key必须是一个原子操作
        // 且谁拥有锁，谁释放
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = new Object();
        try {
            result = jedis.eval(script, Collections.singletonList(key),
                    Collections.singletonList(value));
            if (RELEASE_SUCCESS.equals(result)) {
                log.info("release lock success, value:{}", value);
                return true;
            }
        } catch (Exception e) {
            log.error("release lock error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        log.info("release lock failed, value:{}, result:{}", value, result);
        return false;
    }
}
