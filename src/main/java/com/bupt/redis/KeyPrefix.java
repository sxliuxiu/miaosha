package com.bupt.redis;
/**
 * 定义前缀接口
 * */
public interface KeyPrefix {
    /**
     *设置key 的过期时间
     */
    public int expireSeconds();
    /**
     * 定义key的前缀
     * */
    public String getPrefix();

}
