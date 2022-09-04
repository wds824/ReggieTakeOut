package com.wds.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wds.reggie.dto.DishDto;
import com.wds.reggie.entity.Dish;
import com.wds.reggie.entity.DishFlavor;
import com.wds.reggie.mapper.DishMapper;
import com.wds.reggie.service.DishFlavorService;
import com.wds.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-21 19:42
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService flavorService;

    /**
     * 存储 Dish并存储 Flavor
     *
     * @param dto Dish and Flavor
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dto) {
        this.save(dto);

        List<DishFlavor> flavors = dto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dto.getId());
            return item;
        }).collect(Collectors.toList());

        flavorService.saveBatch(flavors);
    }

    /**
     * 查询菜品的List的flavors
     */
    @Override
    public DishDto queryWithFlavors(Long id) {
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(DishFlavor::getDishId, id);
        dishDto.setFlavors(flavorService.list(queryWrapper));

        return dishDto;
    }

    /**
     * 修改数据并修改dto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dto) {
        this.updateById(dto);
        //清空原有的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dto.getId());
        flavorService.remove(queryWrapper);
        //保存更改后的口味
        List<DishFlavor> flavors = dto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dto.getId());
            return item;
        }).collect(Collectors.toList());

        flavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品和菜品对应的口味
     */
    @Override
    public void deleteWithFlavor(List<Long> list) {
        if (list == null || list.size() == 0)
            return;
        //清除菜品
        this.removeByIds(list);
        //清除口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, list);
        flavorService.remove(queryWrapper);
    }
}
