package com.ucas.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ucas.seckill.exception.GlobalException;
import com.ucas.seckill.mapper.TUserMapper;
import com.ucas.seckill.pojo.TUser;
import com.ucas.seckill.service.ITUserService;
import com.ucas.seckill.utils.CookieUtil;
import com.ucas.seckill.utils.MD5Util;
import com.ucas.seckill.utils.UUIDUtil;
import com.ucas.seckill.utils.ValidatorUtil;
import com.ucas.seckill.vo.LoginVo;
import com.ucas.seckill.vo.RespBean;
import com.ucas.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qingjiusanliangsan
 * @since 2022-06-05
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements ITUserService {

    @Autowired
    private TUserMapper tUserMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLongin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//        System.out.println(loginVo);
        //参数校验
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //TODO 因为我懒测试的时候，把手机号码和密码长度校验去掉了，可以打开。页面和实体类我也注释了，记得打开
        if (!ValidatorUtil.isMobile(mobile)) {
            throw new GlobalException(RespBeanEnum.MOBILE_ERROR);
        }
        TUser user = tUserMapper.selectById(mobile);
        if (user == null) {
            System.out.println("Error--------user == null");
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确
//        System.out.println(MD5Util.formPassToDBPass(password, user.getSalt()));
        if (!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        System.out.println("----------正确----------");

        //生成Cookie
        String userTicket = UUIDUtil.uuid();
        //将用户信息存入redis
        redisTemplate.opsForValue().set("user:" + userTicket, user);

//        //将用户信息存入request
//        request.getSession().setAttribute(userTicket, user);
        CookieUtil.setCookie(request, response, "userTicket", userTicket);

        return RespBean.success(userTicket);

    }

    @Override
    public TUser getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        TUser user = (TUser) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        TUser user = getUserByCookie(userTicket, request, response);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int result = tUserMapper.updateById(user);
        if (1 == result) {
            //删除Redis
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }


}
