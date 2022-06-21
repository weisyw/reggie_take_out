package com.itww.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itww.reggie.entity.Employee;
import com.itww.reggie.mapper.EmployeeMapper;
import com.itww.reggie.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: ww
 * @DateTime: 2022/6/17 15:45
 * @Description: This is description of class
 */

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {


    @Override
    public Page pageShow(int page, int pageSize, String name) {
        // 创建分页构造器
        Page pageInfo = new Page(page, pageSize);
        // 创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        // 添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        this.page(pageInfo, queryWrapper);
        return pageInfo;
    }
}
