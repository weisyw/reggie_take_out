package com.itww.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.itww.reggie.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: ww
 * @DateTime: 2022/6/19 19:15
 * @Description: This is description of class
 */
@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
}
