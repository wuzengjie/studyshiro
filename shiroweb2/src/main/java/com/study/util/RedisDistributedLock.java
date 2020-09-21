package com.study.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
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
 *
 * 用于集群中定时任务的处理
 *
 * 单节点的情况
 */
@Component
public class RedisDistributedLock {
    private static Logger log= LoggerFactory.getLogger(RedisDistributedLock.class);
    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    // 锁的超时时间
    private static int EXPIRE_TIME = 5 * 10000;
    // 锁等待时间
    private static int WAIT_TIME = 1 * 1000;

    private String key;

    private Jedis jedis;

    @Resource
    private JedisPool jedisPool;

    private Jedis getResource(){
        return jedisPool.getResource();
    }

    // 不断尝试加锁
    public String lock( String key) {
        try {
            this.key=key;
            this.jedis=getResource();
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
        /*看起来很完美啊，但是如果你判断的时候锁是自己持
        有的，这时锁超时自动释放了。然后又被其他客户端重
        新上锁，然后当前线程执行到jedis.del(key)，
        这样这个线程不就删除了其他线程上的锁嘛，好像有点乱套了哦！*/
        // 判断加锁与解锁是不是同一个客户端
        //if (requestId.equals(jedis.get(lockKey))) {
        // 若在此时，这把锁突然不是这个客户端的，则会误解锁
        //jedis.del(lockKey);
        //}
        this.jedis=getResource();
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
