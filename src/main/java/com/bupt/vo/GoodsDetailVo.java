package com.bupt.vo;

import com.bupt.domain.MiaoShaUser;

public class GoodsDetailVo {
    private int miaoshaStatus;
    private int remainSeconds;
    private GoodsVo goods;
    private MiaoShaUser user;

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoodsVo() {
        return goods;
    }

    public void setGoodsVo(GoodsVo goods) {
        this.goods = goods;
    }

    public MiaoShaUser getUser() {
        return user;
    }

    public void setUser(MiaoShaUser user) {
        this.user = user;
    }
}
