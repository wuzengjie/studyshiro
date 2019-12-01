package com.study.cache;

import com.study.util.JedisUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

@Component
public class RedisCache<K,V> implements Cache<K,V> {

    /*//根据Key获取缓存中的值
    public V get(K key) throws CacheException;
    //往缓存中放入key-value，返回缓存中之前的值
    public V put(K key, V value) throws CacheException;
    //移除缓存中key对应的值，返回该值
    public V remove(K key) throws CacheException;
    //清空整个缓存
    public void clear() throws CacheException;
    //返回缓存大小
    public int size();
    //获取缓存中所有的key
    public Set<K> keys();
    //获取缓存中所有的value
    public Collection<V> values(); */

    private final String SHIRO_SESSION_PREFIX="STUDYSHIRO-SESSION";

    @Resource
    private JedisUtil jedisUtil;

    private byte[] getKey(K k){
        if(k instanceof  String){
            return (SHIRO_SESSION_PREFIX+k).getBytes();
        }
        return SerializationUtils.serialize(k);
    }

    @Override
    public V get(K k) throws CacheException {
        System.out.println("从缓存中取数据------"+k);
        byte[] key=getKey(k);
        byte[] value=jedisUtil.get(key);
        if(value!=null){
            System.out.println("对象为：-----------"+(V)SerializationUtils.deserialize(value));
            return (V)SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public V put(K k, V v) throws CacheException {

        byte[] key=getKey(k);
        byte[] value=SerializationUtils.serialize(v);
        jedisUtil.set(key,value);
        jedisUtil.expire(key,600);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        byte[] key=getKey(k);
        byte[] value=jedisUtil.get(key);
        jedisUtil.del(key);
        if(value!=null){
            return (V)SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }
}
