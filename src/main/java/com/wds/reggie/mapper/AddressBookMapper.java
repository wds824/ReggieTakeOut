package com.wds.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wds.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-09-02 13:20
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
