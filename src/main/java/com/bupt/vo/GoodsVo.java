package com.bupt.vo;

import com.bupt.domain.Goods;

import java.util.Date;

/**
 * Goods表中只有商品的相关信息，miaosha_goods表中只有秒杀的相关信息和商品id
 * 没有商品的详细信息
 * 为了方便秒杀订单的描述，创造一个class将这两个表结合起来
 * */
public class GoodsVo extends Goods{
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
