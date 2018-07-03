package com.bupt.service;

import com.bupt.domain.MiaoShaUser;
import com.bupt.domain.OrderInfo;
import com.bupt.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoShaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    /*@Autowired
    GoodsDao goodsDao;*/

    @Transactional
    public OrderInfo miaosha(MiaoShaUser user, GoodsVo goods) {
        //减库存  下订单  写入秒杀订单
        goodsService.reduceStock(goods);
        //order_info miaosha_order
        return orderService.createOrder(user,goods);


        /*
        提倡在service中使用自己的Dao，否则使用别人的Service
        Goods goods = new Goods();
        goods.setId(goodsVo.getId());
        goods.setGoodsStock(goodsVo.getGoodsStock()-1);
        goodsDao.reduceStock(goods);*/
    }
}
