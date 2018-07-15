package com.bupt.service;

import com.bupt.Util.MD5Util;
import com.bupt.Util.UUIDUtil;
import com.bupt.domain.MiaoShaOrder;
import com.bupt.domain.MiaoShaUser;
import com.bupt.domain.OrderInfo;
import com.bupt.redis.MiaoShaKey;
import com.bupt.redis.RedisService;
import com.bupt.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class MiaoShaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    /*@Autowired
    GoodsDao goodsDao;*/

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoShaUser user, GoodsVo goods) {
        //减库存  下订单  写入秒杀订单
        boolean success = goodsService.reduceStock(goods);
        if (success){
            //order_info miaosha_order
            return orderService.createOrder(user,goods);
        }else {
            //对售完的商品进行标记
            setGoodsOver(goods.getId());
            return null;
        }



        /*
        提倡在service中使用自己的Dao，否则使用别人的Service
        Goods goods = new Goods();
        goods.setId(goodsVo.getId());
        goods.setGoodsStock(goodsVo.getGoodsStock()-1);
        goodsDao.reduceStock(goods);*/
    }



    public long getMiaoShaResult(Long userId, long goodsId) {
        MiaoShaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        if (order != null){
            return order.getOrderId();
        }else {
            //判断商品是否销售完毕
            boolean isOver = getGoodsOver(goodsId);
            if (isOver){
                return -1;
            }else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoShaKey.isGoodsOver,""+goodsId,true);
    }

    //如果缓存中存在该键，说明已经售完
    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoShaKey.isGoodsOver,""+goodsId);
    }

    public String createMiaoShaPath(MiaoShaUser user,long goodsId) {
        if (user == null||goodsId <= 0){
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoShaKey.getMiaoShaPath,""+user.getId()+"_"+goodsId,str);
        return str;
    }

    //检查请求的路径是否和设置的路径一致
    public boolean checkPath(MiaoShaUser user, long goodsId, String path) {
        if (user == null||path == null){
            return false;
        }
        String pathOld = redisService.get(MiaoShaKey.getMiaoShaPath,""+user.getId()+"_"+goodsId,String.class);
        return path.equals(pathOld);
    }

    public BufferedImage createMiaoShaVerifyCode(MiaoShaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoShaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }
    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    public boolean checkVerifyCode(MiaoShaUser user, long goodsId, int verifyCode) {
        if (user == null||goodsId <= 0){
            return false;
        }
        Integer codeOld = redisService.get(MiaoShaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId,Integer.class);
        if (codeOld == null || codeOld-verifyCode!=0){
            return false;
        }
        //删掉存储在redis中的验证码，防止下次再使用
        redisService.del(MiaoShaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId);
        return false;
    }
}
