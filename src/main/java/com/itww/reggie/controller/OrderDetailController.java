package com.itww.reggie.controller;

import com.itww.reggie.service.OrdersDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: ww
 * @DateTime: 2022/6/21 12:09
 * @Description: This is description of class
 */

@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {

    @Autowired
    private OrdersDetailService ordersDetailService;
}
