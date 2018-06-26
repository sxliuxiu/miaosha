package com.bupt.controller;

import com.bupt.domain.User;
import com.bupt.redis.RedisService;
import com.bupt.result.CodeMsg;
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
    public Result<Long> redisGet(){
        Long v1 = redisService.get("key1",Long.class);
        return Result.success(v1);
    }
}
