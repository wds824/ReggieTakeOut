package com.wds.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wds.reggie.common.utils.BaseContext;
import com.wds.reggie.common.R;
import com.wds.reggie.entity.AddressBook;
import com.wds.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
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
    public R<String> save(@RequestBody AddressBook addressBook) {
        Long id = BaseContext.getCurrentId().getUserId();
        addressBook.setUserId(id);
        addressBookService.save(addressBook);
        return R.success("添加成功!");
    }

    /**
     * 查询当前用户的地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId().getUserId());
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook) {
        if (addressBook.getId() == null) {
            return R.error("网络异常，请稍后再试!");
        }
        //取消原有默认的地址
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getIsDefault, 1);
        updateWrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(updateWrapper);

        //设置当前地址为默认
        LambdaUpdateWrapper<AddressBook> updateWrapper1 = new LambdaUpdateWrapper<>();
        updateWrapper1.eq(AddressBook::getId, addressBook.getId());
        updateWrapper1.set(AddressBook::getIsDefault, 1);
        addressBookService.update(updateWrapper1);

        return R.success("修改成功！");
    }

    /**
     * 获取默认收货地址
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        List<AddressBook> books = addressBookService.list(queryWrapper);
        if (books == null || books.size() == 0) {
            return R.error("您还没有收货地址。");
        }
        return R.success(books.get(0));
    }

    /**
     * 回显收货地址
     *
     * @param id 收货地址ID
     */
    @GetMapping("/{id}")
    public R<AddressBook> reShow(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 更新地址信息
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }
}

