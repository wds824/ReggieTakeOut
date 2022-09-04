package com.wds.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wds.reggie.common.R;
import com.wds.reggie.dto.DishDto;
import com.wds.reggie.entity.Category;
import com.wds.reggie.entity.Dish;
import com.wds.reggie.entity.DishFlavor;
import com.wds.reggie.entity.SetmealDish;
import com.wds.reggie.service.CategoryService;
import com.wds.reggie.service.DishFlavorService;
import com.wds.reggie.service.DishService;
import com.wds.reggie.service.SetmealDishService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-22 12:12
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 分页查询
     * 查询dish后根据categoryId查category并将dto.setCategoryNam ->category.name 并二次包装Page对象
     *
     * @param page     几页
     * @param pageSize 每页几条
     * @param name     搜索关键字
     * @return page{dto}
     */
    @GetMapping("/page")
    public R<Page<DishDto>> pagination(Integer page, Integer pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        dishService.page(dishPage, queryWrapper);

        Page<DishDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(dishPage, dtoPage, "records");

        List<Dish> records = dishPage.getRecords();

        List<DishDto> newRecords = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Category category = categoryService.getById(item.getCategoryId());
            if (category == null) {
                return dishDto;
            }
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(newRecords);
        return R.success(dtoPage);
    }


    /**
     * id查询
     * 修改回显
     */
    @GetMapping("/{id}")
    public R<DishDto> reShow(@PathVariable Long id) {
        DishDto dishDto = dishService.queryWithFlavors(id);
        return R.success(dishDto);
    }

    /**
     * 添加新的菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dto) {
        dishService.saveWithFlavor(dto);
        return R.success("添加成功!");
    }


    /**
     * 更新
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dto) {
        dishService.updateWithFlavor(dto);
        return R.success("修改成功!");
    }

    @PostMapping("/status/{status}")
    public R<String> setStatus(@PathVariable Integer status, Long[] ids) {
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getStatus, status);

        List<Long> list = Arrays.asList(ids);
        updateWrapper.in(Dish::getId, list);

        dishService.update(updateWrapper);
        return R.success("修改售卖状态成功！");
    }

    /**
     * 删除菜品
     * 被套餐绑定的菜品无法删除
     *
     * @param ids 要删除的菜品ids
     */
    @DeleteMapping
    public R<String> remove(Long[] ids) {
        boolean isAllOk = true;
        List<Long> list =new ArrayList<>(Arrays.asList(ids));

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getDishId, list);
        List<SetmealDish> dishList = setmealDishService.list(queryWrapper);

        if (dishList != null) {
            for (SetmealDish setmealDish : dishList) {
                Long id = setmealDish.getDishId();
                if (list.contains(id)) {
                    list.remove(id);
                    isAllOk = false;
                }
            }
        }
        dishService.deleteWithFlavor(list);

        if (isAllOk) {
            return R.success("删除成功!");
        }
        return R.error("部分菜品因被套餐绑定，无法删除!");
    }


    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 根据条件查询菜品数据
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        查询菜品
        queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
//          给菜品封装口味数据
        List<DishDto> dtoList = list.stream().map((item) -> {
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(item, dto);
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> flavors = dishFlavorService.list(queryWrapper1);
            dto.setFlavors(flavors);
            return dto;
        }).collect(Collectors.toList());

        return R.success(dtoList);
    }
}
