package com.itww.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itww.reggie.common.CustomException;
import com.itww.reggie.entity.Category;
import com.itww.reggie.entity.Dish;
import com.itww.reggie.entity.Setmeal;
import com.itww.reggie.mapper.CategoryMapper;
import com.itww.reggie.service.CategoryService;
import com.itww.reggie.service.DishService;
import com.itww.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: ww
 * @DateTime: 2022/6/19 15:53
 * @Description: 分类
 */


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类id查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishLambdaQueryWrapper);
        // 查询当前分类是否关联了菜品，如果已关联，抛出一个业务异常
        if (count > 0) {
            // 已经关联菜品，抛出异常
            throw new CustomException("当前分类关联了菜品，无法删除");
        }
        // 查询当前分类是否关联了套餐，如果已关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if (count1 > 0) {
            // 已经关联套餐，抛出异常
            throw new CustomException("当前分类关联了套餐，无法删除");
        }
        // 正常删除分类
        super.removeById(id);
    }
}
