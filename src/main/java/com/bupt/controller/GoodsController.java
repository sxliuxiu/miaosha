package com.bupt.controller;

import com.bupt.dao.GoodsDao;
import com.bupt.domain.MiaoShaUser;
import com.bupt.redis.RedisService;
import com.bupt.service.GoodsService;
import com.bupt.service.MiaoShaUserService;
import com.bupt.service.UserService;
import com.bupt.vo.GoodsVo;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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


    @RequestMapping("/to_list")
    public String toList(Model model,MiaoShaUser user){
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsList);
        return "goods_list";
    }


}
