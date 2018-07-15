package com.bupt.controller;

import com.bupt.domain.User;
import com.bupt.rabbitmq.MQSender;
import com.bupt.redis.RedisService;
import com.bupt.redis.UserKey;
import com.bupt.result.Result;
import com.bupt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;
    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","Joshua");
        return "hello";
    }

    @RequestMapping("db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("db/tx")
    @ResponseBody
    public Result<User> dbTx(){
        userService.tx();
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User user = redisService.get(UserKey.getById,""+1,User.class);
        return Result.success(user);
    }

    @RequestMapping("redis/set")
    @ResponseBody
    public Result<User> redisSet(){
        /**
         * 因为User类没有下边的这么一个构造函数，所以不能直接进行创建对象，需要使用set方法
         * User user = new User(1,"1111");
         * */
        User user = new User();
        user.setId(1);
        user.setName("1111");
        Boolean ret= redisService.set(UserKey.getById,""+1,user);
        User user1 = redisService.get(UserKey.getById,""+1,User.class);


        return Result.success(user1);
    }
    @RequestMapping("mq")
    @ResponseBody
    public Result<String> mq(){
        mqSender.send("hello bupt");
        return Result.success("Hello bupt");
    }
}
