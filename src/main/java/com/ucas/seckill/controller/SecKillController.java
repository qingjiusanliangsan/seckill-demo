package com.ucas.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ucas.seckill.config.AccessLimit;
import com.ucas.seckill.exception.GlobalException;
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
import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author qingjiusanliangsan
 * create 2022-06-06-22:42
 */

@Slf4j
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
    @Autowired
    private RedisScript<Long> redisScript;


    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    /**
     * ????????????????????????????????????????????????Redis
     *
     * @param
     * @return void
     * @author LiChao
     * @operation add
     * @date 6:29 ?????? 2022/3/8
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

    @ApiOperation("???????????????")
    @GetMapping(value = "/captcha")
    public void verifyCode(TUser tUser, Long goodsId, HttpServletResponse response) {
        if (tUser == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //???????????????????????????????????????
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //???????????????
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + tUser.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("?????????????????????", e.getMessage());
        }
    }

    /**
     * ??????????????????
     *
     * @param tuser
     * @param goodsId
     * @param captcha
     * @return com.example.seckilldemo.vo.RespBean
     * @author LiChao
     * @operation add
     * @date 4:04 ?????? 2022/3/9
     **/
    @ApiOperation("??????????????????")
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @GetMapping(value = "/path")
    @ResponseBody
    public RespBean getPath(TUser tuser, Long goodsId, String captcha, HttpServletRequest request) {
        if (tuser == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();
//        ?????????????????????5????????????5???
//        String uri = request.getRequestURI();
//        captcha = "0";
//        Integer count = (Integer) valueOperations.get(uri + ":" + tuser.getId());
//        if (count == null) {
//            valueOperations.set(uri + ":" + tuser.getId(), 1, 5, TimeUnit.SECONDS);
//        } else if (count < 5) {
//            valueOperations.increment(uri + ":" + tuser.getId());
//        } else {
//            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
//        }

        boolean check = orderService.checkCaptcha(tuser, goodsId, captcha);
        if (!check) {
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String str = orderService.createPath(tuser, goodsId);
        return RespBean.success(str);
    }


    /**
     * ??????????????????
     *
     * @param tUser
     * @param goodsId
     * @return orderId ?????? ???-1 ???????????? ???0 ?????????
     * @author LiChao
     * @operation add
     * @date 7:04 ?????? 2022/3/8
     **/
//    @ApiOperation("??????????????????")
    @GetMapping("getResult")
    @ResponseBody
    public RespBean getResult(TUser tUser, Long goodsId) {
        if (tUser == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(tUser, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * ????????????
     *
     * @param user
     * @param goodsId
     * @return java.lang.String
     * @author LC
     * @operation add
     * @date 11:36 ?????? 2022/3/4
     **/
    @ApiOperation("????????????")
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path, TUser user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //???????????????
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        //????????????????????????
        TSeckillOrder tSeckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (tSeckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //?????????????????????Redis?????????
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //????????????
//        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        if (stock < 0) {
            EmptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessag = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessag));
        return RespBean.success(0);
    }


    @RequestMapping(value="/doSeckill3",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(TUser user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        //???????????????
        ValueOperations valueOperations = redisTemplate.opsForValue();


        //????????????????????????
        TSeckillOrder tSeckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (tSeckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //?????????????????????Redis?????????
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //????????????
//        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
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
        //????????????
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //????????????????????????
//        TSeckillOrder seckillOrder = seckillOrderService.getOne(new
//                QueryWrapper<TSeckillOrder>().eq("user_id", user.getId()).eq(
//                "goods_id",
//                goodsId));
        //????????????????????????
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
        //????????????
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckillFail";
        }
        //????????????????????????
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