package com.ucas.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ucas.seckill.pojo.TUser;
import com.ucas.seckill.vo.LoginVo;
import com.ucas.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qingjiusanliangsan
 * @since 2022-06-05
 */
public interface ITUserService extends IService<TUser> {
    /**
     * 登录方法
     * @param loginVo
     * @param request
     * @param response
     * @return com.example.seckilldemo.vo.RespBean
     * @operation add
     **/
    RespBean doLongin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户
     * @param userTicket
     * @return com.example.seckilldemo.entity.TUser
     * @operation add
     **/
    TUser getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    /**
     * 更新密码
     * @param userTicket
     * @param password
     * @return com.example.seckilldemo.vo.RespBean
     * @operation add
     **/
    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
