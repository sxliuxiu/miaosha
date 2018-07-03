package com.bupt.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 * 用于生成Redispool
 * 使用redis就需要获取jedis对象，而jedis由JedisPool可以生成
 * 所以设置一个工厂类生成JedisPool。并将这个类交给spring管理
 * */
@Service
public class RedisPoolFactory {
    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool jedisPoolFactory(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);

        /*JedisPool jp = new JedisPool(poolConfig,redisConfig.getHost(),redisConfig.getPort(),redisConfig.getTimeout()*1000,
                redisConfig.getPassword(),0);*/
        JedisPool jp = new JedisPool(poolConfig,redisConfig.getHost(),redisConfig.getPort());
        return jp;
    }
}
