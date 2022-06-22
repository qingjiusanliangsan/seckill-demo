package com.ucas.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ucas.seckill.mapper.TSeckillOrderMapper;
import com.ucas.seckill.pojo.TSeckillOrder;
import com.ucas.seckill.service.ITSeckillOrderService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 秒杀订单表 服务实现类
 * </p>
 *
 * @author qingjiusanliangsan
 * @since 2022-06-06
 */
@Service
public class TSeckillOrderServiceImpl extends ServiceImpl<TSeckillOrderMapper, TSeckillOrder> implements ITSeckillOrderService {

}
