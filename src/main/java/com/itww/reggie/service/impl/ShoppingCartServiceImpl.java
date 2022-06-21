package com.itww.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itww.reggie.common.BaseContext;
import com.itww.reggie.entity.ShoppingCart;
import com.itww.reggie.mapper.ShoppingCartMapper;
import com.itww.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author: ww
 * @DateTime: 2022/6/20 21:55
 * @Description: This is description of class
 */

@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        // 设置用户id，指定是哪个用户的购物车数据
        long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if (dishId != null) {
            // 添加菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            // 添加套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        // 查询当前菜品或套餐是否在当前购物车中
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one != null) {
            // 如果存在就在原基础上加加
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);
        } else {
            // 如果不存在，则添加到购物车，数量为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return one;
    }

    @Override
    public void sub(ShoppingCart shoppingCart) {
        long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(shoppingCart.getDishId()!=null, ShoppingCart::getDishId,shoppingCart.getDishId());
        queryWrapper.eq(shoppingCart.getSetmealId()!=null, ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);

        if (one.getNumber() == 1){
            // 如果只有一个就把他删掉
            shoppingCartService.removeById(one.getId());
        }else {
            // 否则就减1
            shoppingCart.setId(one.getId());
            shoppingCart.setNumber(one.getNumber() -1);
            shoppingCartService.updateById(shoppingCart);
        }
    }
}
