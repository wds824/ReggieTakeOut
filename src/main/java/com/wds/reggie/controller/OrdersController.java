package com.wds.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wds.reggie.common.BaseContext;
import com.wds.reggie.common.R;
import com.wds.reggie.entity.Orders;
import com.wds.reggie.exception.CustomException;
import com.wds.reggie.service.AddressBookService;
import com.wds.reggie.service.OrdersService;
import com.wds.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
     * 历史订单
     */
    @GetMapping("userPage")
    public R<Page<Orders>> userPage(int page, int pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(ordersPage, queryWrapper);

        return R.success(ordersPage);
    }

    /**
     * @param page      第几页
     * @param pageSize  每页多少条
     * @param number    订单号
     * @param beginTime 创建时间 开始
     * @param endTime   结束
     */
    @GetMapping("page")
    public R<Page<Orders>> ordersPage(int page, int pageSize,
                                      Long number, String beginTime, String endTime) {


        Page<Orders> page1 = new Page<>(page, pageSize);
        // condition
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(number != null, Orders::getNumber, number);
        //has time range
        if (StringUtils.isNotEmpty(beginTime) && StringUtils.isNotEmpty(endTime)) {
            LocalDateTime localBeginTime = LocalDateTime.parse(beginTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime localEndTime = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            queryWrapper.gt(Orders::getOrderTime, localBeginTime);
            queryWrapper.lt(Orders::getOrderTime, localEndTime);

        }

        Page<Orders> ordersPage = ordersService.page(page1, queryWrapper);
        return R.success(ordersPage);
    }

    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        if (orders.getId() == null || orders.getStatus() == null){
            throw new CustomException("你给我传的啥??");
        }
        LambdaUpdateWrapper<Orders> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Orders::getStatus, orders.getStatus());
        updateWrapper.eq(Orders::getId, orders.getId());
        ordersService.update(updateWrapper);
        return R.success("修改成功！");
    }
}
