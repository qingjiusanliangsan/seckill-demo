package com.ucas.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ucas.seckill.pojo.TGoods;
import com.ucas.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author qingjiusanliangsan
 * @since 2022-06-06
 */
public interface TGoodsMapper extends BaseMapper<TGoods> {
    /**
     * 返回商品列表
     * @author LC
     * @operation add
     * @date 5:50 下午 2022/3/3
     * @param
     * @return java.util.List<com.example.seckilldemo.vo.GoodsVo>
     **/
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVobyGoodsId(Long goodsId);
}
