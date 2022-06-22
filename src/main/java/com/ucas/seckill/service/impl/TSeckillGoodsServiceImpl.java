package com.ucas.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ucas.seckill.mapper.TSeckillGoodsMapper;
import com.ucas.seckill.pojo.TSeckillGoods;
import com.ucas.seckill.service.ITSeckillGoodsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 秒杀商品表 服务实现类
 * </p>
 *
 * @author qingjiusanliangsan
 * @since 2022-06-06
 */
@Service
public class TSeckillGoodsServiceImpl extends ServiceImpl<TSeckillGoodsMapper, TSeckillGoods> implements ITSeckillGoodsService {

}
