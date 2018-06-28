package com.bupt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * 定义一个WebMvcConfigurerAdapter
 * */

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{

    /**
     * 将resolver注册进来
     * */
    @Autowired
    UserArgumentResolver userArgumentResolver;
    /**
     *controller中可以携带很多参数，这些参数的值是通过框架回调addArgumentResolvers方法
     * 想controller中的参数进行赋值
     * */
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }
}
