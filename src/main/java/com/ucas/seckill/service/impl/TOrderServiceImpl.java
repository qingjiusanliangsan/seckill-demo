package com.ucas.seckill.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ucas.seckill.exception.GlobalException;
import com.ucas.seckill.mapper.TOrderMapper;
import com.ucas.seckill.pojo.TOrder;
import com.ucas.seckill.pojo.TSeckillGoods;
import com.ucas.seckill.pojo.TSeckillOrder;
import com.ucas.seckill.pojo.TUser;
import com.ucas.seckill.service.ITGoodsService;
import com.ucas.seckill.service.ITOrderService;
import com.ucas.seckill.service.ITSeckillGoodsService;
import com.ucas.seckill.service.ITSeckillOrderService;
import com.ucas.seckill.utils.MD5Util;
import com.ucas.seckill.utils.UUIDUtil;
import com.ucas.seckill.vo.GoodsVo;
import com.ucas.seckill.vo.OrderDeatilVo;
import com.ucas.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qingjiusanliangsan
 * @since 2022-06-06
 */
@Service
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements ITOrderService {
    @Autowired
    private ITSeckillGoodsService seckillGoodsService;
    @Autowired
    private ITGoodsService goodsService;
    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    private ITSeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    @Override
    @Transactional
    public TOrder seckill(TUser user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //秒杀商品表减库存
        TSeckillGoods seckillGoods = seckillGoodsService.getOne(new
                QueryWrapper<TSeckillGoods>().eq("goods_id",
                goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
//        seckillGoodsService.updateById(seckillGoods);
        boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<TSeckillGoods>()
                .setSql("stock_count = " + "stock_count-1")
                .eq("goods_id", goods.getId())
                .gt("stock_count", 0)
        );

//        if (!seckillGoodsResult) {
//            return null;
//        }

        if (seckillGoods.getStockCount() < 1) {
            //判断是否还有库存
            valueOperations.set("isStockEmpty:" + goods.getId(), "0");
            return null;
        }
        //生成订单
        TOrder order = new TOrder();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        TSeckillOrder secKillOrder = new TSeckillOrder();
        secKillOrder.setOrderId(order.getId());
        secKillOrder.setUserId(user.getId());
        secKillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(secKillOrder);

        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), secKillOrder);

        return order;
    }


    @Override
    public OrderDeatilVo detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        TOrder tOrder = orderMapper.selectById(orderId);
        GoodsVo goodsVobyGoodsId = goodsService.findGoodsVoByGoodsId(tOrder.getGoodsId());
        OrderDeatilVo orderDeatilVo = new OrderDeatilVo();
        orderDeatilVo.setTOrder(tOrder);
        orderDeatilVo.setGoodsVo(goodsVobyGoodsId);
        return orderDeatilVo;
    }

    @Override
    public String createPath(TUser user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, str, 1, TimeUnit.MINUTES);
        return str;
    }

    @Override
    public boolean checkPath(TUser user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(path)) {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    @Override
    public boolean checkCaptcha(TUser user, Long goodsId, String captcha) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(captcha)) {
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }

}
