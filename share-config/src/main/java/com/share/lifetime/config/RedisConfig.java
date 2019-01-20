package com.share.lifetime.config;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author liaoxiang
 * @date 2019/01/17
 */
@Configuration
@PropertySources(value = {@PropertySource(value = "classpath:config/db/redis-common.properties", encoding = "UTF-8"),
    @PropertySource(value = "classpath:config/db/redis-${spring.profiles.active}.properties", encoding = "UTF-8")})
public class RedisConfig {

    @Autowired
    private Environment env;

    @Profile(value = {"local"})
    @Bean(name = "jedisConnectionFactory")
    @DependsOn(value = {"jedisPoolConfig"})
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setDatabase(0);
        // redis服务器地址
        connectionFactory.setHostName(env.getProperty("redis.host", String.class));
        // redis端口
        connectionFactory.setPort(env.getProperty("redis.port", Integer.class));
        // redis访问密码
        connectionFactory.setPassword(env.getProperty("redis.pass", String.class));
        connectionFactory.setPoolConfig(jedisPoolConfig);
        connectionFactory.setTimeout(2000);
        connectionFactory.setUsePool(true);
        return connectionFactory;
    }

    @Profile(value = {"local", "dev", "prod"})
    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 最大空闲连接数
        poolConfig.setMaxIdle(env.getProperty("redis.maxIdle", Integer.class));
        // 最大连接数
        poolConfig.setMaxTotal(env.getProperty("redis.maxTotal", Integer.class));
        // 获取连接时的最大等待毫秒数, 默认-1
        poolConfig.setMaxWaitMillis(env.getProperty("redis.maxWaitMillis", Integer.class));
        // 逐出连接的最小空闲时间 默认1000L * 60L * 30L毫秒(30分钟)
        poolConfig.setMinEvictableIdleTimeMillis(env.getProperty("redis.minEvictableIdleTimeMillis", Integer.class));
        // 最小空闲连接数
        poolConfig.setMinIdle(env.getProperty("redis.minIdle", Integer.class));
        // 在获取连接的时候检查有效性, 默认false
        poolConfig.setTestOnBorrow(env.getProperty("redis.testOnBorrow", Boolean.class));
        return poolConfig;
    }

    @Profile(value = {"local", "dev", "prod"})
    @Bean(name = "redisTemplate")
    @DependsOn("jedisConnectionFactory")
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory);
        StringRedisSerializer keySerializer = new StringRedisSerializer(Charset.forName("UTF-8"));
        template.setKeySerializer(keySerializer);
        JdkSerializationRedisSerializer valueSerializer = new JdkSerializationRedisSerializer();
        template.setValueSerializer(valueSerializer);
        return template;
    }

    @Profile(value = {"dev", "prod"})
    @Bean(name = "redisSentinelConfiguration")
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        String master = env.getProperty("redis.sentinel.master", String.class);
        Set<String> sentinelHostAndPorts = new HashSet<String>();
        sentinelHostAndPorts.add(env.getProperty("redis.sentinel.nodes1", String.class));
        sentinelHostAndPorts.add(env.getProperty("redis.sentinel.nodes2", String.class));
        sentinelHostAndPorts.add(env.getProperty("redis.sentinel.nodes3", String.class));
        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration(master, sentinelHostAndPorts);
        // configuration.setDatabase(0);
        // configuration.setPassword(env.getProperty("redis.pass", String.class));
        return configuration;
    }

    @Profile(value = {"dev", "prod"})
    @Bean(name = "jedisConnectionFactory")
    @DependsOn(value = {"redisSentinelConfiguration", "jedisPoolConfig"})
    public JedisConnectionFactory jedisConnectionFactory_(RedisSentinelConfiguration redisSentinelConfiguration,
        JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory connectionFactory =
            new JedisConnectionFactory(redisSentinelConfiguration, jedisPoolConfig);
        connectionFactory.setDatabase(0);
        connectionFactory.setPassword(env.getProperty("redis.pass", String.class));
        return connectionFactory;
    }

}
