package com.wds.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wds.reggie.common.BaseContext;
import com.wds.reggie.dto.OrdersDto;
import com.wds.reggie.entity.*;
import com.wds.reggie.exception.CustomException;
import com.wds.reggie.mapper.OrdersMapper;
import com.wds.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-09-03 16:43
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;


    /**
     * 提交订单
     * 生成订单明细
     * 清空购物车
     */
    @Override
    public void submit(Orders order) {
        Long userId = BaseContext.getCurrentId();
        // 查购物车
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        if (shoppingCarts == null) {
            throw new CustomException("购物车是空的，无法创建订单。");
        }

        //订单id
        long orderID = IdWorker.getId();

        //生成订单明细 计算总金额
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(orderID);
            detail.setNumber(item.getNumber());
            detail.setDishFlavor(item.getDishFlavor());
            detail.setDishId(item.getDishId());
            detail.setSetmealId(item.getSetmealId());
            detail.setName(item.getName());
            detail.setImage(item.getImage());
            detail.setAmount(item.getAmount());

            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return detail;
        }).collect(Collectors.toList());


        // 生成订单
        OrdersDto dto = new OrdersDto();

        User user = userService.getById(userId);

        AddressBook addressBook = addressBookService.getById(order.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("用户地址信息有误，无法下单。");
        }
        dto.setId(orderID);
        dto.setOrderTime(LocalDateTime.now());
        dto.setCheckoutTime(LocalDateTime.now());
        dto.setStatus(2);
        dto.setUserId(userId);
        dto.setAmount(new BigDecimal(amount.get()));
        dto.setNumber(String.valueOf(orderID));
        dto.setUserName(user.getName());
        dto.setConsignee(addressBook.getConsignee());
        dto.setPhone(user.getPhone());
        dto.setAddressBookId(addressBook.getId());
        dto.setAddress(addressBook.getDetail());

        //存储订单
        this.save(dto);
        //存储订单明细
        orderDetailService.saveBatch(orderDetails);
        // 清空购物车
        shoppingCartService.remove(queryWrapper);
    }
}
