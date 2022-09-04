package com.wds.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wds.reggie.common.BaseContext;
import com.wds.reggie.common.R;
import com.wds.reggie.dto.OrdersDto;
import com.wds.reggie.entity.Orders;
import com.wds.reggie.service.AddressBookService;
import com.wds.reggie.service.OrdersService;
import com.wds.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-09-03 16:48
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private AddressBookService addressBookService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order) {
        ordersService.submit(order);
        return R.success("订单提交成功");
    }


    /**
     *  历史订单
     */
    @GetMapping("userPage")
    public R<Page<Orders>> userPage(int page, int pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        ordersService.page(ordersPage, queryWrapper);

        return R.success(ordersPage);
    }
}
