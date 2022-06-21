package com.itww.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itww.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: ww
 * @DateTime: 2022/6/17 15:43
 * @Description: This is description of class
 */

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
