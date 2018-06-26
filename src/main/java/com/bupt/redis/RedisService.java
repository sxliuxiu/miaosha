package com.bupt.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    @Autowired
    RedisConfig redisConfig;
    public <T>T get(String key,Class<T> clazz){
        Jedis jedis = null;
        //因为这是一个连接池，所以需要进行释放
        try {
            jedis = jedisPool.getResource();
            String str = jedis.get(key);
            //将获取出来的string转换成一个bean
            T t = stringToBean(str,clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean set(String key,T value){
        Jedis jedis = null;
        //因为这是一个连接池，所以需要进行释放
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (str == null||str.length()<0){
                return false;
            }
            jedis.set(key,str);
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    //将传入的参数bean转换成string
    private <T> String beanToString(T value) {
        if (value == null){
            return null;
        }
        //判断传入的参数类型,简单的举几个例子
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class){
            return ""+value;
        }else if(clazz == String.class){
            return (String)value;
        }else if(clazz == long.class||clazz == Long.class){
            return ""+value;
        }else {
            return JSON.toJSONString(value);
        }
    }

    /*
    * 使用fastJson将bean对象转换成字符串存入redis中
    * 现在要将从redis中取出来的string转换成bean
    * */
    private <T> T stringToBean(String str,Class<T> clazz) {
        //必不可少的参数校验
        if (str == null||str.length() <= 0||clazz == null){
            return null;
        }
        if(clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(str);
        }else if(clazz == String.class){
            return (T)String.valueOf(str);
        }else if(clazz == long.class||clazz == Long.class){
            return (T)Long.valueOf(str);
        }else {
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis!=null){
            jedis.close();
        }
    }

    /*
    * 使用redis就需要获取jedis对象，而jedis由JedisPool可以生成
    * 所以设置一个工厂类生成JedisPool。并将这个类交给spring管理
    * */
    @Bean
    public JedisPool jedisPoolFactory(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);

        JedisPool jp = new JedisPool(poolConfig,redisConfig.getHost(),redisConfig.getPort(),redisConfig.getTimeout()*1000,
                redisConfig.getPassword(),0);
        return jp;
    }
}
