package com.itww.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itww.reggie.entity.User;
import com.itww.reggie.mapper.UserMapper;
import com.itww.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: ww
 * @DateTime: 2022/6/20 17:33
 * @Description: This is description of class
 */

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
