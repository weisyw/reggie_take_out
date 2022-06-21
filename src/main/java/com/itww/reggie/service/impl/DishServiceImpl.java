package com.itww.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itww.reggie.dto.DishDto;
import com.itww.reggie.entity.Category;
import com.itww.reggie.entity.Dish;
import com.itww.reggie.entity.DishFlavor;
import com.itww.reggie.mapper.DishMapper;
import com.itww.reggie.service.CategoryService;
import com.itww.reggie.service.DishFlavorService;
import com.itww.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ww
 * @DateTime: 2022/6/19 17:15
 * @Description: This is description of class
 */

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品并插入菜品对应的口味数据。
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到dish
        this.save(dishDto);
        // 菜品id
        Long dishId = dishDto.getId();
        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        // 保存菜品口味数据到dish_flavor
        dishFlavorService.saveBatch(flavors);
    }


    /**
     * 根据id查询菜品信息和对应的口味信息。回显数据
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息，从dish查
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        // 查询当前菜品对应的口味信息，从dish_flavor查
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表
        this.updateById(dishDto);
        // 清理当前菜品对应口味数据 dish_flavor delete
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        // 添加当前提交过来的口味数据 dish_flavor insert
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public Page<DishDto> pageSearch(int page, int pageSize, String name) {
        // 分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        // 创建返回页面的分页
        Page<DishDto> dishDtoPage = new Page<>();
        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        // 排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 分页查询
        // dishService.page(pageInfo, queryWrapper);
        page(pageInfo, queryWrapper);
        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId(); // 分类id
            Category category = categoryService.getById(categoryId); // 根据id查询分类对象
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return dishDtoPage;
    }

    @Override
    public void deleteDish(String ids) {
        String[] idsList = ids.split(",");
        for (String id : idsList) {
            log.info("删除id为 {} 的菜品",id);
            this.removeById(id);
            dishFlavorService.remove(new QueryWrapper<DishFlavor>().eq("dish_id", id));
        }
    }

    @Override
    public void updateStatus(int status, String ids) {
        String[] idsList = ids.split(",");
        for (String id : idsList) {
            Dish dish = new Dish();
            dish.setId(Long.parseLong(id));
            dish.setStatus(status);
            this.updateById(dish);
        }
    }

    @Override
    public List<Dish> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1); // 起售状态
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = this.list(queryWrapper);
        return list;
    }
}
