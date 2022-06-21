package com.itww.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itww.reggie.common.R;
import com.itww.reggie.dto.OrdersDto;
import com.itww.reggie.entity.OrderDetail;
import com.itww.reggie.entity.Orders;
import com.itww.reggie.entity.User;
import com.itww.reggie.service.OrdersDetailService;
import com.itww.reggie.service.OrdersService;
import com.itww.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ww
 * @DateTime: 2022/6/21 12:08
 * @Description: 订单
 */

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrdersDetailService ordersDetailService;
    @Autowired
    private UserService userService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("提交的订单数据：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 获取订单详情
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> getOrders(Integer page, Integer pageSize){
        Page<OrdersDto> ordersDtoPage = ordersService.userPage(page,pageSize);
        return R.success(ordersDtoPage);
    }

    /**
     * 后台员工分页查看订单信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        log.info("page = {},pageSize = {}, number = {}", page, pageSize, number);
        Page<Orders> page1 = ordersService.page(page, pageSize, number, beginTime, endTime);
        return R.success(page1);
    }

    /**
     * 更改订单状态
     * @param ordersDto
     * @return
     */
    @PutMapping
    public R<String> status(@RequestBody OrdersDto ordersDto){
        ordersService.updateStatusById(ordersDto);
        return R.success("修改成功！");
    }
}
