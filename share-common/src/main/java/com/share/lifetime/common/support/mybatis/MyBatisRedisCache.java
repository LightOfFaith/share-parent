package com.share.lifetime.common.support.mybatis;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyBatisRedisCache implements Cache {

    private static RedisTemplate<Object, Object> redisTemplate;

    private final String id;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public MyBatisRedisCache(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
        log.debug("MyBatisRedisCache---->id:{}", id);
    }

    public static void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        MyBatisRedisCache.redisTemplate = redisTemplate;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object key, Object value) {
        BoundHashOperations<Object, Object, Object> boundHashOps = redisTemplate.boundHashOps(id);
        boundHashOps.put(key, value);
        log.debug("MyBatisRedisCache---->putObject---->id:{},key:{},value:{}", id, key, value);

    }

    @Override
    public Object getObject(Object key) {
        BoundHashOperations<Object, Object, Object> boundHashOps = redisTemplate.boundHashOps(id);
        Object value = boundHashOps.get(key);
        log.debug("MyBatisRedisCache---->getObject---->id:{},key:{},value:{}", id, key, value);
        return value;
    }

    @Override
    public Object removeObject(Object key) {
        BoundHashOperations<Object, Object, Object> boundHashOps = redisTemplate.boundHashOps(id);
        Long delete = boundHashOps.delete(key);
        log.debug("MyBatisRedisCache---->removeObject---->id:{},key:{},delete:{}", id, key, delete);
        return delete;
    }

    @Override
    public void clear() {
        redisTemplate.delete(id);
        log.debug("MyBatisRedisCache---->clear---->id:{}", id);
    }

    @Override
    public int getSize() {
        BoundHashOperations<Object, Object, Object> boundHashOps = redisTemplate.boundHashOps(id);
        Map<Object, Object> entries = boundHashOps.entries();
        int size = entries.size();
        log.debug("MyBatisRedisCache---->getSize---->id:{},size:{}", id, size);
        return size;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    @Override
    public String toString() {
        return "MyBatisRedisCache [id=" + id + "]";
    }

}
