package com.wds.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wds.reggie.entity.DishFlavor;
import com.wds.reggie.mapper.DishFlavorMapper;
import com.wds.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-28 18:38
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService {
}
