package com.itww.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itww.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: ww
 * @DateTime: 2022/6/21 12:03
 * @Description: This is description of class
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
