package com.bupt.controller;

import com.bupt.domain.MiaoShaOrder;
import com.bupt.domain.MiaoShaUser;
import com.bupt.domain.OrderInfo;
import com.bupt.redis.RedisService;
import com.bupt.result.CodeMsg;
import com.bupt.service.*;
import com.bupt.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 创建一个商品相关的controller
 * */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    private static Logger log = LoggerFactory.getLogger(MiaoshaController.class);

    @Autowired
    UserService userService;
    @Autowired
    MiaoShaUserService miaoShaUserService;
    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;
    @Autowired
    MiaoShaService miaoShaService;


    @RequestMapping("/do_miaosha")
    public String toList(Model model, MiaoShaUser user,
                         @RequestParam("goodsId")long goodsId){
        model.addAttribute("user",user);
        if (user == null){
            return "login";
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock < 0){
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断用户是否已经秒杀过
        MiaoShaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if (order != null){
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        /**
         * 既有库存，也没秒杀过，则可以进行秒杀。
         * 接下来就是减库存 下订单 写入秒杀订单
         * 很明显可以看出来，上边的三步必须在事务中执行，一个执行剩下的两个必须执行。
         * */
        OrderInfo orderInfo = miaoShaService.miaosha(user,goods);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);
        return "order_detail";
    }
}
