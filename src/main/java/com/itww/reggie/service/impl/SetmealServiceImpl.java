package com.itww.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itww.reggie.common.CustomException;
import com.itww.reggie.dto.DishDto;
import com.itww.reggie.dto.SetmealDto;
import com.itww.reggie.entity.*;
import com.itww.reggie.mapper.SetmealMapper;
import com.itww.reggie.service.CategoryService;
import com.itww.reggie.service.DishService;
import com.itww.reggie.service.SetmealDishService;
import com.itww.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ww
 * @DateTime: 2022/6/19 17:17
 * @Description: This is description of class
 */

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;

    /**
     * 新增套餐同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息 etmeal insert
        this.save(setmealDto);
        // 保存套餐和菜品的关联信息 setmeal_dish insert
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 套餐页面分页显示
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<SetmealDto> pageShow(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        this.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list =  records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            // 分类id
            Long categoryId = item.getCategoryId();
            // 根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return dtoPage;
    }

    /**
     * 更改菜品售卖状态
     * @param status
     * @param ids
     */
    @Override
    public void updateStatus(int status, String ids) {
        String[] idsList = ids.split(",");
        for (String id : idsList) {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(Long.parseLong(id));
            setmeal.setStatus(status);
            this.updateById(setmeal);
        }
    }

    /**
     * 删除套餐，同时删除关联菜品的数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，确定是否可删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        // 如果不能删除，直接抛出异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        // 如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);
        // 删除关系表中数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper1);
    }

    /**
     * 修改页面回显，根据id查找数据
     * @param ids
     * @return
     */
    @Override
    public SetmealDto getData(Long ids) {
        // 查询套餐基本信息
        Setmeal setmeal = this.getById(ids);
        SetmealDto setmealDto = new SetmealDto();
        // 对象拷贝
        BeanUtils.copyProperties(setmeal, setmealDto);
        // 查询当前套餐对应的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, ids);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        String categoryName = categoryService.getById(setmeal.getCategoryId()).getName();
        setmealDto.setCategoryName(categoryName);
        setmealDto.setSetmealDishes(list);
        log.info(list.toString());
        return setmealDto;
    }


    @Override
    @Transactional
    public void update(SetmealDto setmealDto) {
        // 更新setmeal表
        this.updateById(setmealDto);
        // 清理当前套餐对应菜品数据 的、setmeal_dish delete
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        // 添加菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public Dish finalSelect(Long ids) {

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dish::getId, ids);
        Dish one = dishService.getOne(lambdaQueryWrapper);

        return one;

    }
}
