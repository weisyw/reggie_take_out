package com.itww.reggie.common;

/**
 * @Author: ww
 * @DateTime: 2022/6/19 17:31
 * @Description: 自定义业务异常
 */
public class CustomException extends RuntimeException{

    public CustomException(String message) {
        super(message);
    }
}
