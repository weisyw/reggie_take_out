package com.itww.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itww.reggie.dto.DishDto;
import com.itww.reggie.entity.Dish;

import java.util.List;

/**
 * @Author: ww
 * @DateTime: 2022/6/19 17:15
 * @Description: This is description of class
 */
public interface DishService extends IService<Dish> {

    // 新增菜品并插入菜品对应的口味数据。两张表 dish dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    // 根据id查询菜品信息和对应的口味信息实现页面的回显
    public DishDto getByIdWithFlavor(Long id);

    // 更新菜品信息并更新口味信息
    public void updateWithFlavor(DishDto dishDto);

    // 菜品信息分页查询
    public Page<DishDto> pageSearch(int page, int pageSize, String name);

    // 删除菜品
    public void deleteDish(String ids);

    // 根据条件查询对应的菜品数据
    public List<Dish> list(Dish dish);

    // 更改售卖状态
    public void updateStatus(int status, String ids);


}
