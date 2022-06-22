package com.ucas.seckill.controller;

import com.ucas.seckill.service.ITUserService;
import com.ucas.seckill.vo.LoginVo;
import com.ucas.seckill.vo.RespBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author qingjiusanliangsan
 * create 2022-06-05-12:33
 */

@Controller
@RequestMapping("/login")
@Slf4j
@Api(value = "登录", tags = "登录")
public class LoginController {

    @Autowired
    private ITUserService tUserService;

    /**
     * 跳转登录页面
     *
     * @param
     * @return java.lang.String
     * @operation add
     **/
    @ApiOperation("跳转登录页面")
    @RequestMapping(value = "/toLogin", method = RequestMethod.GET)
    public String toLogin() {
        System.out.println("----------------toLogin()");
        return "login";
//        return "hello";
    }

    @ApiOperation("登录接口")
    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("----------------doLogin()");
        log.info("{}", loginVo);
        return tUserService.doLongin(loginVo, request, response);
    }
}
