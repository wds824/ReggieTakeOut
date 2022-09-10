package com.wds.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wds.reggie.common.R;
import com.wds.reggie.dto.SetmealDto;
import com.wds.reggie.entity.Category;
import com.wds.reggie.entity.Setmeal;
import com.wds.reggie.entity.SetmealDish;
import com.wds.reggie.service.CategoryService;
import com.wds.reggie.service.SetmealDishService;
import com.wds.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.1
 * @since 2022-08-30 18:24
 * 1.1  支持缓存
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDisService;
    @Autowired
    private CategoryService categoryService;

    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping()
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);

        return R.success("保存套餐成功!");
    }

    /**
     * 分页
     *
     * @param page     几页
     * @param pageSize 每页
     * @param name     搜索关键字
     */
//    @Cacheable(value = "setmealCache", key = "'page_' + #page + '_' + #pageSize", condition = "#name == null")
    @Cacheable(value = "setmealCache", key = "'page_' + #page + '_' + #pageSize")
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        Page<Setmeal> setmealPage = setmealService.page(pageInfo, queryWrapper);

        Page<SetmealDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(setmealPage, dtoPage, "records");

        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> newRecords = records.stream().map((item) -> {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item, dto);

            Category category = categoryService.getById(item.getCategoryId());
            if (category == null) {
                return dto;
            }
            dto.setCategoryName(category.getName());
            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(newRecords);

        return R.success(dtoPage);
    }

    /**
     * 修改时回显
     *
     * @return disDto
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @GetMapping("/{id}")
    public R<SetmealDto> reShow(@PathVariable Long id) {
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto dto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, dto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, dto.getId());
        List<SetmealDish> dishList = setmealDisService.list(queryWrapper);

        dto.setSetmealDishes(dishList);
        return R.success(dto);
    }

    /**
     * 修改setMeal表
     * 清除setMealDish的旧数据
     * 写入新的serMealDIsh数据
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @PutMapping
    public R<String> update(@RequestBody SetmealDto dto) {
        setmealService.updateById(dto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, dto.getId());
        setmealDisService.remove(queryWrapper);

        List<SetmealDish> newList = setmealDisService.setSetmealId(dto.getId(), dto.getSetmealDishes());
        setmealDisService.saveBatch(newList);

        return R.success("修改成功!");
    }

    /**
     * 停售启售
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping("/status/{type}")
    public R<String> status(@PathVariable int type, Long[] ids) {
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Setmeal::getStatus, type);
        updateWrapper.in(Setmeal::getId, Arrays.asList(ids));
        setmealService.update(updateWrapper);
        return R.success("售卖状态更新成功!");
    }

    /**
     * 删除套餐
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @Transactional
    @DeleteMapping
    public R<String> delete(Long[] ids) {

        List<Long> list = Arrays.asList(ids);
        setmealService.removeByIds(list);


        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, list);
        setmealDisService.remove(queryWrapper);

        return R.success("删除成功!");
    }

    /**
     * 查询套餐
     */
    @Cacheable(value = "setmealCache", key = "'list_'  +  #categoryId + '_' + #status")
    @GetMapping("/list")
    public R<List<Setmeal>> listDish(Long categoryId, Integer status) {
        LambdaQueryWrapper<Setmeal> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Setmeal::getCategoryId, categoryId);
        queryWrapper1.eq(Setmeal::getStatus, status);
        List<Setmeal> list = setmealService.list(queryWrapper1);
        return R.success(list);
    }
}
