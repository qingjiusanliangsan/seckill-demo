package com.ucas.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ucas.seckill.pojo.TSeckillOrder;
import com.ucas.seckill.pojo.TUser;

/**
 * <p>
 * 秒杀订单表 服务类
 * </p>
 *
 * @author qingjiusanliangsan
 * @since 2022-06-06
 */
public interface ITSeckillOrderService extends IService<TSeckillOrder> {
    /**
     * 获取秒杀结果
     *
     * @param tUser
     * @param goodsId
     * @return orderId 成功 ；-1 秒杀失败 ；0 排队中
     **/
    Long getResult(TUser tUser, Long goodsId);
}
