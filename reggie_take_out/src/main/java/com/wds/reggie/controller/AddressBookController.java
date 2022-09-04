package com.wds.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wds.reggie.common.BaseContext;
import com.wds.reggie.common.R;
import com.wds.reggie.entity.AddressBook;
import com.wds.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-09-02 13:43
 */
@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        Long id = BaseContext.getCurrentId();
        addressBook.setUserId(id);
        addressBookService.save(addressBook);
        return R.success("添加成功!");
    }

    /**
     * 查询当前用户的地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        if (addressBook.getId() == null) {
            return R.error("网络异常，请稍后再试!");
        }
        //取消原有默认的地址
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getIsDefault, 1);
        updateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(updateWrapper);

        //设置当前地址为默认
        LambdaUpdateWrapper<AddressBook> updateWrapper1 = new LambdaUpdateWrapper<>();
        updateWrapper1.eq(AddressBook::getId, addressBook.getId());
        updateWrapper1.set(AddressBook::getIsDefault,1);
        addressBookService.update(updateWrapper1);

        return R.success("修改成功！");
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault(){

        AddressBook addressBook = addressBookService.getOne(
                new LambdaQueryWrapper<AddressBook>().eq(AddressBook::getIsDefault, 1));

        return R.success(addressBook);
    }
}

