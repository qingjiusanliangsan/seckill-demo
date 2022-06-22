package com.ucas.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ucas.seckill.pojo.TOrder;
import com.ucas.seckill.pojo.TUser;
import com.ucas.seckill.vo.GoodsVo;
import com.ucas.seckill.vo.OrderDeatilVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qingjiusanliangsan
 * @since 2022-06-06
 */
public interface ITOrderService extends IService<TOrder> {
    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    TOrder seckill(TUser user, GoodsVo goods);

    OrderDeatilVo detail(Long orderId);
}
