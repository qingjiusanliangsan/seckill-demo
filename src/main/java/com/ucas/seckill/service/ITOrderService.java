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

    /**
     * 获取秒杀地址
     *
     * @param user
     * @param goodsId
     * @return java.lang.String
     * @author LiChao
     * @operation add
     * @date 2:59 下午 2022/3/9
     **/
    String createPath(TUser user, Long goodsId);

    /**
     * 校验秒杀地址
     *
     * @param user
     * @param goodsId
     * @param path
     * @return boolean
     * @author LiChao
     * @operation add
     * @date 2:59 下午 2022/3/9
     **/
    boolean checkPath(TUser user, Long goodsId, String path);

    /**
     * 校验验证码
     * @author LiChao
     * @operation add
     * @date 3:52 下午 2022/3/9
     * @param tuser
     * @param goodsId
     * @param captcha
     * @return boolean
     **/
    boolean checkCaptcha(TUser tuser, Long goodsId, String captcha);
}
