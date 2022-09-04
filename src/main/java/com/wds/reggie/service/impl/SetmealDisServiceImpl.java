package com.wds.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wds.reggie.entity.SetmealDish;
import com.wds.reggie.mapper.SetmealDishMapper;
import com.wds.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-30 18:26
 */
@Service
public class SetmealDisServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
    /**
     * 批处理SetMealId
     */
    @Override
    public List<SetmealDish> setSetmealId(Long id, List<SetmealDish> setmealDishes) {
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());
        return setmealDishes;
    }
}
