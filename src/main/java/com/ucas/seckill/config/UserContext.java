package com.ucas.seckill.config;

import com.ucas.seckill.pojo.TUser;

/**
 * @author qingjiusanliangsan
 * create 2022-06-25-17:11
 */

public class UserContext {

    private static ThreadLocal<TUser> userThreadLocal = new ThreadLocal<>();

    public static void setUser(TUser tUser) {
        userThreadLocal.set(tUser);
    }

    public static TUser getUser() {
        return userThreadLocal.get();
    }
}