package com.itww.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itww.reggie.common.R;
import com.itww.reggie.dto.DishDto;
import com.itww.reggie.dto.SetmealDto;
import com.itww.reggie.entity.*;
import com.itww.reggie.mapper.DishMapper;
import com.itww.reggie.mapper.SetmealDishMapper;
import com.itww.reggie.service.CategoryService;
import com.itww.reggie.service.DishService;
import com.itww.reggie.service.SetmealDishService;
import com.itww.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: ww
 * @DateTime: 2022/6/20 11:40
 * @Description: 套餐管理
 */


@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;


    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true) // 清理所有缓存数据
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("新增套餐信息：{}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }


    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<SetmealDto> pageInfo = setmealService.pageShow(page, pageSize, name);
        return R.success(pageInfo);
    }

    /**
     * 更改菜品售卖状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("status/{status}")
    public R<String> updateStatus(@PathVariable int status, String ids){
        setmealService.updateStatus(status,ids);
        return R.success("状态更改成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true) // 清理所有缓存数据
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("要删除的套餐信息为:{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }

    /**
     * 修改页面回显，根据id查找数据
     * @param ids
     * @return
     */
    @GetMapping("/{ids}")
    public R<SetmealDto> getData(@PathVariable Long ids){
        SetmealDto setmealDto = setmealService.getData(ids);
        return R.success(setmealDto);
    }

    /**
     * 更新套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "setmealCache", allEntries = true) // 清理所有缓存数据
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.update(setmealDto);
        return R.success("更新成功");
    }


//    @GetMapping("/list")
//    public R getList(Long categoryId,Integer status){
//        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Setmeal::getStatus,status).eq(Setmeal::getCategoryId,categoryId);
//        List<Setmeal> list = setmealService.list(wrapper);
//        return R.success(list);
//    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }


    /**
     * 套餐详情
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> getDish(@PathVariable Long id){
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setmeal_id", id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(queryWrapper);
        List<Long> list = new ArrayList<>();
        for (SetmealDish item : setmealDishes) {
            list.add(item.getDishId());
        }
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.in("id", list);
        List<Dish> dishes = dishMapper.selectList(dishQueryWrapper);
        List<DishDto> dishDtos = dishes.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            return dishDto;
        }).collect(Collectors.toList());

        for (DishDto dishDto : dishDtos) {
            for (SetmealDish setmealDish : setmealDishes) {
                if(setmealDish.getDishId().equals(dishDto.getId())){
                    dishDto.setCopies(setmealDish.getCopies());
                }
            }
        }
        return R.success(dishDtos);
    }
}
