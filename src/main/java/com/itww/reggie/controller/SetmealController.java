package com.itww.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itww.reggie.common.R;
import com.itww.reggie.dto.DishDto;
import com.itww.reggie.dto.SetmealDto;
import com.itww.reggie.entity.*;
import com.itww.reggie.service.CategoryService;
import com.itww.reggie.service.DishService;
import com.itww.reggie.service.SetmealDishService;
import com.itww.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
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
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }


    @GetMapping("/dish/{ids}")
    public R<List<Dish>> getSetMeal(@PathVariable Long ids){
        Setmeal setmeal = setmealService.getById(ids);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        log.info("所有的关联菜品：{}",setmealDishList.toString());
        List<Dish> dishList = null;
        for (SetmealDish setmealDish : setmealDishList) {
            LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Dish::getId, setmealDish.getDishId());
            dishList = dishService.list(lambdaQueryWrapper);
        }
        return R.success(dishList);
    }
}
