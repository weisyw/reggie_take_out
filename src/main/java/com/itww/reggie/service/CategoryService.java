package com.itww.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itww.reggie.entity.Category;

/**
 * @Author: ww
 * @DateTime: 2022/6/19 15:52
 * @Description: This is description of class
 */

public interface CategoryService extends IService<Category> {

    public void remove(Long ids);
}
