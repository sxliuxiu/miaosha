package com.bupt.controller;

import com.bupt.domain.MiaoShaUser;
import com.bupt.domain.OrderInfo;
import com.bupt.redis.RedisService;
import com.bupt.result.CodeMsg;
import com.bupt.result.Result;
import com.bupt.service.GoodsService;
import com.bupt.service.MiaoShaUserService;
import com.bupt.service.OrderService;
import com.bupt.vo.GoodsVo;
import com.bupt.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, MiaoShaUser user,
                                      @RequestParam("orderId") long orderId){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //查询是否存在订单
        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        //得到商品的编号
        long goodsId = order.getGoodsId();
        //查找出秒杀商品相关信息
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        /**
         * 将订单详细信息和秒杀商品相关信息存到一起
         * */
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrderInfo(order);
        vo.setGoods(goods);

        return Result.success(vo);
    }
}
