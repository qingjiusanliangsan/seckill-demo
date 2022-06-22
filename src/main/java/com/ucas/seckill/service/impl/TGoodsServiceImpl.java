package com.ucas.seckill.service.impl;

import com.ucas.seckill.pojo.TGoods;
import com.ucas.seckill.mapper.TGoodsMapper;
import com.ucas.seckill.service.ITGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ucas.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author qingjiusanliangsan
 * @since 2022-06-06
 */
@Service
public class TGoodsServiceImpl extends ServiceImpl<TGoodsMapper, TGoods> implements ITGoodsService {
    @Autowired
    private TGoodsMapper tGoodsMapper;

    @Override
    public List<GoodsVo> findGoodsVo() {
        return tGoodsMapper.findGoodsVo();
    }

    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return tGoodsMapper.findGoodsVobyGoodsId(goodsId);
    }
}
