package com.ucas.seckill.service;

import com.ucas.seckill.pojo.TGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ucas.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author qingjiusanliangsan
 * @since 2022-06-06
 */
public interface ITGoodsService extends IService<TGoods> {
    /**
     * 返回商品列表
     *
     * @param
     * @return java.util.List<com.example.seckilldemo.vo.GoodsVo>
     * @author LC
     * @operation add
     * @date 5:49 下午 2022/3/3
     **/
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     *
     * @param goodsId
     * @return java.lang.String
     * @author LC
     * @operation add
     * @date 9:37 上午 2022/3/4
     **/
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
