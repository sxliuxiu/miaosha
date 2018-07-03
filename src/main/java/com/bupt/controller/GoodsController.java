package com.bupt.controller;

import com.bupt.dao.GoodsDao;
import com.bupt.domain.MiaoShaUser;
import com.bupt.redis.RedisService;
import com.bupt.service.GoodsService;
import com.bupt.service.MiaoShaUserService;
import com.bupt.service.UserService;
import com.bupt.vo.GoodsVo;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.util.List;

/**
 * 创建一个商品相关的controller
 * */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    GoodsService goodsService;

    @Autowired
    MiaoShaUserService miaoShaUserService;


    @RequestMapping("/to_list")
    public String toList(HttpServletResponse response,Model model,
                         @CookieValue(value = MiaoShaUserService.COOKIE_NAME_TOKEN,required = false) String cookieToken,
                         @RequestParam(value = MiaoShaUserService.COOKIE_NAME_TOKEN,required = false) String paramToken){
        //如果都是空，直接返回登录页面
        if (StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
            return "login";
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        MiaoShaUser user = miaoShaUserService.getByToken(response,token);
        model.addAttribute("user",user);
        return "goods_list";
    }

}
