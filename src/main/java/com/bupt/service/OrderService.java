package com.bupt.service;

import com.bupt.dao.OrderDao;
import com.bupt.domain.MiaoShaOrder;
import com.bupt.domain.MiaoShaUser;
import com.bupt.domain.OrderInfo;
import com.bupt.redis.OrderKey;
import com.bupt.redis.RedisService;
import com.bupt.vo.GoodsVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    /**
     * 根据商品id和用户id查找相关的订单
     * */
    public MiaoShaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {

        //return orderDao.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        //进行缓存优化,直接在缓存中进行查找，不对数据库进行相关的访问。
        return redisService.get(OrderKey.getMiaoShaOrderByUidGid,""+userId+"_"+goodsId,MiaoShaOrder.class);
    }

    /**
     * 根据订单id查找订单的详细信息
     * */
    public OrderInfo getOrderById(long orderId) {

        return orderDao.getOrderById(orderId);
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

        /**
         * 插入成功后，mybatis会把id塞入orderInfo对象中，
         * 所以可以在下边直接从对象中直接获得Id
         */
        orderDao.insert(orderInfo);
        //设置miaosha_order
        MiaoShaOrder miaoShaOrder = new MiaoShaOrder();
        miaoShaOrder.setGoodsId(goods.getId());
        miaoShaOrder.setOrderId(orderInfo.getId());
        miaoShaOrder.setUserId(user.getId());

        orderDao.insertMiaoShaOrder(miaoShaOrder);
        //下单成功后，将秒杀订单存入缓存中
        redisService.set(OrderKey.getMiaoShaOrderByUidGid,""+user.getId()+"_"+goods.getId(),miaoShaOrder);

        return orderInfo;
    }


}
