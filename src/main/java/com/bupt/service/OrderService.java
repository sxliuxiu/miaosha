package com.bupt.service;

import com.bupt.dao.OrderDao;
import com.bupt.domain.MiaoShaGoods;
import com.bupt.domain.MiaoShaOrder;
import com.bupt.domain.MiaoShaUser;
import com.bupt.domain.OrderInfo;
import com.bupt.vo.GoodsVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    public MiaoShaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {

        return orderDao.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
    }

    @Transactional
    public OrderInfo createOrder(MiaoShaUser user, GoodsVo goods) {
        //创建详细订单表
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        //获取插入order_info的表单编号
        long orderId = orderDao.insert(orderInfo);
        //设置miaosha_order
        MiaoShaOrder miaoShaOrder = new MiaoShaOrder();
        miaoShaOrder.setGoodsId(goods.getId());
        miaoShaOrder.setOrderId(orderId);
        miaoShaOrder.setUserId(user.getId());

        orderDao.insertMiaoShaOrder(miaoShaOrder);
        return orderInfo;
    }
}
