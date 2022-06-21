package com.itww.reggie.common;

/**
 * @Author: ww
 * @DateTime: 2022/6/19 15:26
 * @Description: 基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static long getCurrentId() {
        return threadLocal.get();
    }

}
