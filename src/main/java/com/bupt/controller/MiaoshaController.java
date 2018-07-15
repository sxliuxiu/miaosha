package com.bupt.controller;

import com.bupt.acces.AccessLimit;
import com.bupt.domain.MiaoShaOrder;
import com.bupt.domain.MiaoShaUser;
import com.bupt.rabbitmq.MQSender;
import com.bupt.rabbitmq.MiaoShaMessage;
import com.bupt.redis.GoodsKey;
import com.bupt.redis.RedisService;
import com.bupt.result.CodeMsg;
import com.bupt.result.Result;
import com.bupt.service.*;
import com.bupt.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建一个商品相关的controller
 * */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{

    /**
     * 增加一个标记位，对商品是否售完进行内存标记，可以减少对redis的访问
     * */
    private Map<Long,Boolean> localOverMap = new HashMap<Long,Boolean>();
    /**
     * 对秒杀接口进行优化。系统初始化
     * 系统初始化的时候回调该方法
     * 这样在系统初始化的时候将商品的数量添加到缓存中
     * */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList == null)
            return;
        for (GoodsVo goods:goodsList){
            redisService.set(GoodsKey.getMiaoShaGoodsStock,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }

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

    @Autowired
    MQSender sender;


   /* @RequestMapping("/do_miaosha")
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
        *//**
         * 既有库存，也没秒杀过，则可以进行秒杀。
         * 接下来就是减库存 下订单 写入秒杀订单
         * 很明显可以看出来，上边的三步必须在事务中执行，一个执行剩下的两个必须执行。
         * *//*
        OrderInfo orderInfo = miaoShaService.miaosha(user,goods);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);
        return "order_detail";*/

    //实现秒杀页面静态化，也就是使用传统的ajax请求
    @RequestMapping(value = "/{path}/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoShaUser user,
                                   @RequestParam("goodsId")long goodsId,
                                   @PathVariable("path")String path){
        model.addAttribute("user",user);
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check = miaoShaService.checkPath(user,goodsId,path);
        if (!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //预减库存.先获取现有的缓存数量
        long stock = redisService.dec(GoodsKey.getMiaoShaGoodsStock,""+goodsId);
        //如果库存小于0，则直接返回秒杀失败
        if (stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //判断用户是否已经秒杀过
        MiaoShaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if (order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //入队。将秒杀信息入队
        MiaoShaMessage message = new MiaoShaMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        sender.sendMiaoShaMessage(message);
        return Result.success(0);//0表示排队中

        /*//判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock < 0){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断用户是否已经秒杀过
        MiaoShaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if (order != null){
           return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //减库存  下订单  写入秒杀订单
        OrderInfo orderInfo = miaoShaService.miaosha(user,goods);

        return Result.success(orderInfo);*/
    }
    /**
     * 客户端进行轮询
     * orderId:成功
     * -1：秒杀失败
     *  0：排队中
     * */

    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoShaUser user,
                                   @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = miaoShaService.getMiaoShaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    //设置标签对访问次数进行控制，这样可以在不同的方法上通过设置标签进行相应的处理
    @AccessLimit(seconds = 5,maxCount = 5,needLogin = true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoShaPath(Model model, MiaoShaUser user,
                                      @RequestParam("goodsId")long goodsId,
                                         @RequestParam("verifyCode")int verifyCode) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //检查验证码
        boolean check = miaoShaService.checkVerifyCode(user,goodsId,verifyCode);
        if (!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = miaoShaService.createMiaoShaPath(user,goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoShaVerifyCode(HttpServletResponse response,Model model, MiaoShaUser user,
                                               @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image = miaoShaService.createMiaoShaVerifyCode(user,goodsId);
        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(image,"JPEG",out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }
}
