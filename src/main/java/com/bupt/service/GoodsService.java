package com.bupt.service;

import com.bupt.dao.GoodsDao;
import com.bupt.domain.MiaoShaGoods;
import com.bupt.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    //根据查出来的物品对秒杀物品进行减库存
    public boolean reduceStock(GoodsVo goods) {
        MiaoShaGoods good = new MiaoShaGoods();
        good.setGoodsId(goods.getId());
        int ret = goodsDao.reduceStock(good);
        return ret > 0;
    }
}
