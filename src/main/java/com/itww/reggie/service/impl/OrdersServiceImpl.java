package com.itww.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itww.reggie.common.BaseContext;
import com.itww.reggie.common.CustomException;
import com.itww.reggie.dto.OrdersDto;
import com.itww.reggie.entity.*;
import com.itww.reggie.mapper.OrdersMapper;
import com.itww.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author: ww
 * @DateTime: 2022/6/21 12:04
 * @Description: This is description of class
 */

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrdersDetailService ordersDetailService;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrdersService ordersService;
    /**
     * 用户下单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        // 获取当前用户的id
        long currentId = BaseContext.getCurrentId();

        // 查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        if (shoppingCarts == null && shoppingCarts.size() == 0) {
            throw  new CustomException("购物车为空，无法下单");
        }
        // 查询用户数据
        User user = userService.getById(currentId);
        // 查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("地址信息有误，无法下单");
        }
        // 订单号
        long orderId = IdWorker.getId();
        // 向订单表插入数据，一条
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        ordersDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }

    @Override
    public Page<OrdersDto> userPage(Integer page, Integer pageSize) {
        Page<Orders> orders = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        Long userId = BaseContext.getCurrentId();
        User user = userService.getById(userId);
        AddressBook address = addressBookService.getOne(
                new LambdaQueryWrapper<AddressBook>().eq(AddressBook::getUserId, userId).eq(AddressBook::getIsDefault, true));
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId,userId);
        page(orders, wrapper);
        List<OrdersDto> records = orders.getRecords().stream().map(item->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            ordersDto.setUserName(user.getName());
            BeanUtils.copyProperties(address, ordersDto);
            List<OrderDetail> list = ordersDetailService.list(
                    new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, item.getId()));
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(orders, ordersDtoPage);
        ordersDtoPage.setRecords(records);
        return ordersDtoPage;
    }

    /**
     * 修改订单状态
     * @param ordersDto
     */
    @Override
    public void updateStatusById(OrdersDto ordersDto) {
        Long id = ordersDto.getId();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId,id);
        Orders orders = ordersMapper.selectOne(queryWrapper);
        orders.setStatus(ordersDto.getStatus());
        ordersMapper.updateById(orders);
    }


    @Override
    public Page<Orders> page(int page, int pageSize, String number, String beginTime, String endTime) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        lambdaQueryWrapper.between(beginTime != null && endTime != null, Orders::getOrderTime, beginTime, endTime);
        //查询订单基本信息
        Page<Orders> ordersPage = ordersService.page(pageInfo, lambdaQueryWrapper);
        //把基本信息拷贝到OrdersDto对象中
        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");

        List<Orders> ordersRecords = ordersPage.getRecords();
        List<OrdersDto> ordersDtoList = ordersRecords.stream().map((orderRecord) -> {
            OrdersDto ordersDto = new OrdersDto();
            //拷贝对象
            BeanUtils.copyProperties(orderRecord, ordersDto);
            //获取订单id
            Long orderId = orderRecord.getId();
            //通过订单id查询该订单下对应的菜品/套餐
            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> orderDetails = ordersDetailService.list(queryWrapper);
            ordersDto.setOrderDetails(orderDetails);
            //根据用户id获取用户名
            Long userId = orderRecord.getUserId();
            User user = userService.getById(userId);
            if (StringUtils.isNotEmpty(user.getName())) {
                ordersDto.setUserName(user.getName());
            }
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(ordersDtoList);
        return ordersPage;
    }
}
