package com.itww.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itww.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: ww
 * @DateTime: 2022/6/20 17:33
 * @Description: This is description of class
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
