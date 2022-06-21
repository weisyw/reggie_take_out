package com.itww.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itww.reggie.dto.OrdersDto;
import com.itww.reggie.entity.Orders;

/**
 * @Author: ww
 * @DateTime: 2022/6/21 12:04
 * @Description: This is description of class
 */
public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);

    /**
     * 获取订单详情
     * @param page
     * @param pageSize
     * @return
     */
    Page<OrdersDto> userPage(Integer page, Integer pageSize);

    /**
     * 通过订单号修改订单状态
     * @param ordersDto
     */
    void updateStatusById(OrdersDto ordersDto);

    /**
     * 后台订单显示
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    Page<Orders> page(int page, int pageSize, String number, String beginTime, String endTime);
}
