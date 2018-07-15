package com.bupt.rabbitmq;

import com.bupt.domain.MiaoShaUser;

/**
 * 秒杀message
 * 包括秒杀用户和秒杀的商品id
 * */
public class MiaoShaMessage {
    private MiaoShaUser user;
    private long goodsId;

    public MiaoShaUser getUser() {

        return user;
    }

    public void setUser(MiaoShaUser user) {

        this.user = user;
    }

    public long getGoodsId() {

        return goodsId;
    }

    public void setGoodsId(long goodsId) {

        this.goodsId = goodsId;
    }
}
