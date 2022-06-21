package com.itww.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itww.reggie.dto.SetmealDto;
import com.itww.reggie.entity.Dish;
import com.itww.reggie.entity.Setmeal;

import java.util.List;

/**
 * @Author: ww
 * @DateTime: 2022/6/19 17:14
 * @Description: This is description of class
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 套餐页面分页显示
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    public Page<SetmealDto> pageShow(int page, int pageSize, String name);

    /**
     * 更改菜品售卖状态
     * @param status
     * @param ids
     */
    public void updateStatus(int status, String ids);

    /**
     * 删除套餐，同时删除关联菜品的数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);


    /**
     * 修改页面回显，根据id查找数据
     * @param ids
     * @return
     */
    public SetmealDto getData(Long ids);


    /**
     * 更新套餐信息
     * @param setmealDto
     */
    public void update(SetmealDto setmealDto);


    public Dish finalSelect(Long ids);

}
