package com.wds.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wds.reggie.entity.AddressBook;
import com.wds.reggie.mapper.AddressBookMapper;
import com.wds.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-09-02 13:41
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper,AddressBook> implements AddressBookService {
}
