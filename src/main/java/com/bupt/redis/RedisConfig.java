package com.bupt.redis;
/*
* 将redis的配置参数读进来
* */
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
//下边这个注解使spring进行扫描
@Component
@ConfigurationProperties(prefix = "redis")
//根据上边的注解将以redis开头的配置读取进来
public class RedisConfig {
    private String host;
    private int port;
    private int timeout;//秒
    //private String password;
    private int poolMaxTotal;
    private int poolMaxIdle;
    private int poolMaxWait;//秒

    public String getHost() {

        return host;
    }

    public void setHost(String host) {

        this.host = host;
    }

    public int getPort() {

        return port;
    }

    public void setPort(int port) {

        this.port = port;
    }

    public int getTimeout() {

        return timeout;
    }

    public void setTimeout(int timeout) {

        this.timeout = timeout;
    }

    public int getPoolMaxTotal() {

        return poolMaxTotal;
    }

    public void setPoolMaxTotal(int poolMaxTotal) {

        this.poolMaxTotal = poolMaxTotal;
    }

    public int getPoolMaxIdle() {

        return poolMaxIdle;
    }

    public void setPoolMaxIdle(int poolMaxIdle) {

        this.poolMaxIdle = poolMaxIdle;
    }

    public int getPoolMaxWait() {

        return poolMaxWait;
    }

    public void setPoolMaxWait(int poolMaxWait) {
        this.poolMaxWait = poolMaxWait;
    }
}
