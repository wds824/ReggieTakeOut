package com.wds.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wds.reggie.dto.DishDto;
import com.wds.reggie.entity.Dish;

import java.util.List;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-21 19:41
 */

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dto);


    DishDto queryWithFlavors(Long id);

    void updateWithFlavor(DishDto dto);

    void deleteWithFlavor(List<Long> list);
}
