package com.bupt.config;

import com.bupt.acces.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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

    @Autowired
    AccessInterceptor accessInterceptor;
    /**
     *controller中可以携带很多参数，这些参数的值是通过框架回调addArgumentResolvers方法
     * 想controller中的参数进行赋值
     * */
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }

    //将前边定义好的拦截器注册进来
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
    }
}
