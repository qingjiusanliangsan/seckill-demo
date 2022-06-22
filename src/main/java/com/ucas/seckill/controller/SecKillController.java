package com.ucas.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ucas.seckill.pojo.TOrder;
import com.ucas.seckill.pojo.TSeckillOrder;
import com.ucas.seckill.pojo.TUser;
import com.ucas.seckill.rabbitmq.MQSender;
import com.ucas.seckill.service.ITGoodsService;
import com.ucas.seckill.service.ITOrderService;
import com.ucas.seckill.service.ITSeckillOrderService;
import com.ucas.seckill.utils.JsonUtil;
import com.ucas.seckill.vo.GoodsVo;
import com.ucas.seckill.vo.RespBean;
import com.ucas.seckill.vo.RespBeanEnum;
import com.ucas.seckill.vo.SeckillMessage;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.util.CollectionUtils;

import java.util.*;


/**
 * @author qingjiusanliangsan
 * create 2022-06-06-22:42
 */

@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {
    @Autowired
    private ITGoodsService goodsService;
    @Autowired
    private ITSeckillOrderService seckillOrderService;
    @Autowired
    private ITOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;

//    git config user.name "qingjiusanliangsan"
//    git config user.email "2394753336@qq.com"

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    /**
     * 系统初始化，把商品库存数量加载到Redis
     *
     * @param
     * @return void
     * @author LiChao
     * @operation add
     * @date 6:29 下午 2022/3/8
     **/
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }
//    /**
//     * 获取秒杀结果
//     *
//     * @param tUser
//     * @param goodsId
//     * @return orderId 成功 ；-1 秒杀失败 ；0 排队中
//     * @author LiChao
//     * @operation add
//     * @date 7:04 下午 2022/3/8
//     **/
////    @ApiOperation("获取秒杀结果")
//    @GetMapping("getResult")
//    @ResponseBody
//    public RespBean getResult(TUser tUser, Long goodsId) {
//        if (tUser == null) {
//            return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        }
//        Long orderId = seckillOrderService.getResult(tUser, goodsId);
//        return RespBean.success(orderId);
//    }

    @RequestMapping(value="/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(TUser user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        //优化后代码
        ValueOperations valueOperations = redisTemplate.opsForValue();


        //判断是否重复抢购
        TSeckillOrder tSeckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (tSeckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //内存标记，减少Redis的访问
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
//        Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        if (stock < 0) {
            EmptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessag = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessag));

        return RespBean.success(0);

        /*
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购
//        TSeckillOrder seckillOrder = seckillOrderService.getOne(new
//                QueryWrapper<TSeckillOrder>().eq("user_id", user.getId()).eq(
//                "goods_id",
//                goodsId));
        //判断是否重复抢购
        TSeckillOrder seckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);

        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        TOrder order = orderService.seckill(user, goods);

        System.out.println("-----------------doSeckill");
        return RespBean.success(order);
         */
    }

    @RequestMapping(value="/doSeckill2",method = RequestMethod.POST)
    public String doSeckill2(Model model, TUser user, Long goodsId) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckillFail";
        }
        //判断是否重复抢购
        TSeckillOrder seckillOrder = seckillOrderService.getOne(new
                QueryWrapper<TSeckillOrder>().eq("user_id", user.getId()).eq(
                "goods_id",
                goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "seckillFail";
        }
        TOrder order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }
}