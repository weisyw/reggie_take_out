package com.itww.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itww.reggie.entity.DishFlavor;
import com.itww.reggie.mapper.DishFlavorMapper;
import com.itww.reggie.mapper.DishMapper;
import com.itww.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @Author: ww
 * @DateTime: 2022/6/19 19:17
 * @Description: This is description of class
 */

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
