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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-22 12:12
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 分页查询 缓存支持
     * 查询dish后根据categoryId查category并将dto.setCategoryNam ->category.name 并二次包装Page对象
     *
     * @param page     几页
     * @param pageSize 每页几条
     * @param name     搜索关键字
     */
    @GetMapping("/page")
    public R<Page<DishDto>> pagination(Integer page, Integer pageSize, String name) {
        String key = "dish_page_" + page + "_" + pageSize;
        Page<DishDto> dtoPage = null;

        //搜索name字段为空 缓存
        if (name == null) {
            dtoPage = (Page<DishDto>) redisTemplate.opsForValue().get(key);
            if (dtoPage != null) {
                log.info("get redis: {}", key);
                return R.success(dtoPage);
            }
        }

        Page<Dish> dishPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        dishService.page(dishPage, queryWrapper);

        dtoPage = new Page<>();
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

        // save cache
        if (name == null) {
            int randomTime = new Random().nextInt(10) + 60;
            redisTemplate.opsForValue().set(key, dtoPage, randomTime, TimeUnit.MINUTES);
            log.info("save cache:{} life:{}", key, randomTime);
        }

        return R.success(dtoPage);
    }


    /**
     * id查询
     * 修改回显
     * 修改完成之后会清除缓存没必要去缓存
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
        //清除缓存
        Set<Object> keys = redisTemplate.keys("dish_*");

        if (keys != null) {
            redisTemplate.delete(keys);
        }

        dishService.saveWithFlavor(dto);
        return R.success("添加成功!");
    }


    /**
     * 更新
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dto) {
        //清除缓存
        Set<Object> keys = redisTemplate.keys("dish_*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }

        dishService.updateWithFlavor(dto);


        return R.success("修改成功!");
    }

    @PostMapping("/status/{status}")
    public R<String> setStatus(@PathVariable Integer status, Long[] ids) {
        //清除缓存
        Set<Object> keys = redisTemplate.keys("dish_*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }

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
        //清除缓存
        Set<Object> keys = redisTemplate.keys("dish_*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }

        boolean isAllOk = true;
        List<Long> list = new ArrayList<>(Arrays.asList(ids));

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
     * 缓存支持
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        String key = "dish_list_" + dish.getCategoryId() + "_" + dish.getStatus();
        List<DishDto> dtoList = null;
        //查询缓存
        dtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //缓存存在
        if (dtoList != null) {
            log.info("get redis: {}", key);
            return R.success(dtoList);
        }
        // 查询mysql

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 查询菜品
        queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        //  给菜品封装口味数据
        dtoList = list.stream().map((item) -> {
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(item, dto);
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> flavors = dishFlavorService.list(queryWrapper1);
            dto.setFlavors(flavors);
            return dto;
        }).collect(Collectors.toList());


        //缓存查询结果  失效时间60 + random() 分钟
        int randomTime = new Random().nextInt(10) + 60;
        redisTemplate.opsForValue().set(key, dtoList, randomTime, TimeUnit.MINUTES);
        log.info("select sql and save cache:{} life:{}", key, randomTime);
        return R.success(dtoList);
    }
}
