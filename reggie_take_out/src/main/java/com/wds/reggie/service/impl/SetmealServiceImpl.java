package com.wds.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wds.reggie.dto.SetmealDto;
import com.wds.reggie.entity.Setmeal;
import com.wds.reggie.entity.SetmealDish;
import com.wds.reggie.mapper.SetmealMapper;
import com.wds.reggie.service.SetmealDishService;
import com.wds.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-22 20:46
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 保存套餐 并更新套餐菜品表
     */
    @Override
    public void saveWithDish(SetmealDto dto) {
        this.save(dto);

        List<SetmealDish> dishList = dto.getSetmealDishes();
        dishList = dishList.stream().map((iteam)->{
            iteam.setSetmealId(dto.getId());
            return iteam;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishList);
    }
}
