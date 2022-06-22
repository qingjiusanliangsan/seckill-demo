package com.ucas.seckill.controller;


import com.ucas.seckill.pojo.TUser;
import com.ucas.seckill.service.ITOrderService;
import com.ucas.seckill.vo.OrderDeatilVo;
import com.ucas.seckill.vo.RespBean;
import com.ucas.seckill.vo.RespBeanEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author qingjiusanliangsan
 * @since 2022-06-06
 */
@Controller
@RequestMapping("/order")
public class TOrderController {
    @Autowired
    private ITOrderService itOrderService;


    @ApiOperation("订单")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public RespBean detail(TUser tUser, Long orderId) {
        if (tUser == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDeatilVo orderDeatilVo = itOrderService.detail(orderId);
        return RespBean.success(orderDeatilVo);
    }
}
