package com.itww.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itww.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: ww
 * @DateTime: 2022/6/19 15:52
 * @Description: This is description of class
 */

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
