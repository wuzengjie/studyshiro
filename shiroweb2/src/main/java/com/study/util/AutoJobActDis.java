package com.study.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
/*
* 处理集群中的事务问题
* */
public class AutoJobActDis{

    @Autowired
    private JedisUtil jedisUtil;

    public void executeJob() {
        if (rpopRedisFlag("AUTO_JOB_ACTDIS")) {
            /**执行自己的任务*/
            lpushRedisFlag("AUTO_JOB_ACTDIS");
        }
    }

    /**
     * 从缓存中取得Flag，获取定时任务执行权限
     *
     * @param redisKey
     * @return
     */
    public boolean rpopRedisFlag(String redisKey) {
        String redisValue = jedisUtil.rpopRedisList(redisKey);
        if (StringUtils.isEmpty(redisValue)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 缓存中增加Flag
     *
     * @param redisKey
     */
    public void lpushRedisFlag(String redisKey) {
        jedisUtil.lpushRedisList(redisKey, "1");
    }

}
