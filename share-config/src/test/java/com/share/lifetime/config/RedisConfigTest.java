package com.share.lifetime.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RedisConfig.class})
@ActiveProfiles("local")
public class RedisConfigTest {

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testJedisConnectionFactory() {
        log.info("{}", jedisConnectionFactory);
    }

    @Test
    public void testRedisTemplate() {
        String execute = redisTemplate.execute(new RedisCallback<String>() {

            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.ping();
            }

        });
        log.info("execute:{}", execute);
    }

}
