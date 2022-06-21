package com.itww.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itww.reggie.entity.ShoppingCart;

/**
 * @Author: ww
 * @DateTime: 2022/6/20 21:55
 * @Description: This is description of class
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    ShoppingCart add(ShoppingCart shoppingCart);

    void sub(ShoppingCart shoppingCart);
}
