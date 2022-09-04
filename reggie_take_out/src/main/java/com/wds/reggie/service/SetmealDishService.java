package com.wds.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wds.reggie.entity.SetmealDish;

import java.util.List;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-30 18:26
 */
public interface SetmealDishService extends IService<SetmealDish> {

    List<SetmealDish> setSetmealId(Long id, List<SetmealDish> setmealDishes);
}
