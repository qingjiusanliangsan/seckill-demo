package com.ucas.seckill.controller;


import com.ucas.seckill.pojo.TUser;
import com.ucas.seckill.rabbitmq.MQSender;
import com.ucas.seckill.vo.RespBean;
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
 * @since 2022-06-05
 */
@Controller
@RequestMapping("/user")
public class TUserController {
    @Autowired
    private MQSender mqSender;


    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(TUser user){
        return RespBean.success(user);
    }

//    @RequestMapping(value = "/mq", method = RequestMethod.GET)
//    @ResponseBody
//    public void mq() {
//        mqSender.send("Hello rabbitmq");
//    }


//    @RequestMapping(value = "/mq/fanout", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqFanout() {
//        mqSender.send("Hello");
//    }

//    @RequestMapping(value = "/mq/direct01", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqDirect01() {
//        mqSender.send01("Hello Red");
//    }
//
//    @RequestMapping(value = "/mq/direct02", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqDirect02() {
//        mqSender.send02("Hello Green");
//    }
//
//    @RequestMapping(value = "/mq/topic01", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqtopic01() {
//        mqSender.send03("Hello Red");
//    }
//
//    @RequestMapping(value = "/mq/topic02", method = RequestMethod.GET)
//    @ResponseBody
//    public void mqtopic02() {
//        mqSender.send04("Hello Green");
//    }
//
//    @RequestMapping(value = "/mq/header01", method = RequestMethod.GET)
//    @ResponseBody
//    public void header01() {
//        mqSender.send05("Hello 01");
//    }
//
//    @RequestMapping(value = "/mq/header02", method = RequestMethod.GET)
//    @ResponseBody
//    public void header02() {
//        mqSender.send06("Hello 02");
//    }

}
