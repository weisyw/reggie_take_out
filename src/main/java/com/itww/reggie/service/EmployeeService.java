package com.itww.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itww.reggie.entity.Employee;

/**
 * @Author: ww
 * @DateTime: 2022/6/17 15:44
 * @Description: This is description of class
 */
public interface EmployeeService extends IService<Employee> {


    // 员工信息分页显示
    public Page pageShow(int page, int pageSize, String name);

}
