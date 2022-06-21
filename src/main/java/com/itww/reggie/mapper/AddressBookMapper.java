package com.itww.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itww.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: ww
 * @DateTime: 2022/6/20 18:36
 * @Description: This is description of class
 */

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
