package com.wds.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wds.reggie.entity.Category;
import com.wds.reggie.entity.Dish;
import com.wds.reggie.entity.Setmeal;
import com.wds.reggie.exception.CustomException;
import com.wds.reggie.mapper.CategoryMapper;
import com.wds.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-20 21:36
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishServiceImpl dishService;

    @Autowired
    private SetmealServiceImpl setmealService;

    public void remove(Long id) {
        //      当前分类下的菜品数量
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishQueryWrapper);
        if (dishCount > 0) {
            throw new CustomException("请先确保当前分类下没有相关的菜品和套餐");
        }

//      当前分类下的套餐数量
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        int getmealCount = setmealService.count(setmealQueryWrapper);

        if (getmealCount > 0) {
            throw new CustomException("请先确保当前分类下没有相关的套餐");
        }

        this.removeById(id);
    }
}
