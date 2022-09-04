package com.wds.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wds.reggie.entity.Orders;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-09-03 16:42
 */

public interface OrdersService extends IService<Orders> {
    void submit(Orders order);
}
