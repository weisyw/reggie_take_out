package com.itww.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itww.reggie.entity.OrderDetail;
import com.itww.reggie.mapper.OrdersDetailMapper;
import com.itww.reggie.service.OrdersDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: ww
 * @DateTime: 2022/6/21 12:07
 * @Description: This is description of class
 */

@Service
@Slf4j
public class OrdersDetailServiceImpl extends ServiceImpl<OrdersDetailMapper, OrderDetail> implements OrdersDetailService {
}
