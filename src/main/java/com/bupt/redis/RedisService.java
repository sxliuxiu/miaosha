package com.bupt.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    @Autowired
    RedisConfig redisConfig;
    /**
     * 将从redis中取出来的string转换成相应的Bean对象，调用stringToBean方法
     * */
    public <T>T get(KeyPrefix prefix, String key, Class<T> clazz){
        Jedis jedis = null;
        //因为这是一个连接池，所以需要进行释放
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            String str = jedis.get(realKey);
            //将获取出来的string转换成一个bean
            T t = stringToBean(str,clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置对象。将传进来的bean转换成相应的string存入到redis中
     * */
    public <T> boolean set(KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        //因为这是一个连接池，所以需要进行释放
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (str == null||str.length()<0){
                return false;
            }
            String realKey = prefix.getPrefix()+key;
            //获取该键的有效期
            int seconds = prefix.expireSeconds();
            //如果是永不过期
            if(seconds <= 0){
                jedis.set(realKey,str);
            }else {
                jedis.setex(realKey,seconds,str);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断key是否存在
     * */
    public <T> boolean exists(KeyPrefix prefix,String key){
        Jedis jedis = null;
        //因为这是一个连接池，所以需要进行释放
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     *增加值
     * */
    public <T> Long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        //因为这是一个连接池，所以需要进行释放
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少数值
     * */
    public <T> Long dec(KeyPrefix prefix,String key){
        Jedis jedis = null;
        //因为这是一个连接池，所以需要进行释放
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            return jedis.decr(realKey);
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

}
