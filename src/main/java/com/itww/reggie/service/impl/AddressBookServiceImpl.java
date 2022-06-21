package com.itww.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itww.reggie.entity.AddressBook;
import com.itww.reggie.mapper.AddressBookMapper;
import com.itww.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: ww
 * @DateTime: 2022/6/20 18:37
 * @Description: This is description of class
 */

@Service
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
