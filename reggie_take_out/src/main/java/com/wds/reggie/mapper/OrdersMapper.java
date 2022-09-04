package com.wds.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wds.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-09-03 16:40
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
