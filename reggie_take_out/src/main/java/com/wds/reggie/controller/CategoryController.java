package com.wds.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wds.reggie.common.R;
import com.wds.reggie.entity.Category;
import com.wds.reggie.service.impl.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-20 21:41
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryServiceImpl categoryService;



    /**
     * 添加分类
     *
     * @param category 名称 排序 类型
     * @return msg
     */
    @PostMapping
    public R<String> addCategory(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("添加成功。");
    }

    /**
     * 分页查询分类
     *
     * @param page     当前页
     * @param pageSize 每页条数
     * @return R<page
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        Page<Category> categoryPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(categoryPage, queryWrapper);
        return R.success(categoryPage);
    }

    /**
     * 删除分类 约束：分类下无菜品
     *
     * @param id 品类ID
     */
    @DeleteMapping
    public R<String> delete(Long id) {
        categoryService.remove(id);
        return R.success("删除成功");
    }

    /**
     * 修改
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 查对应类型里的分类
     * @param category 类型 菜品还是套餐分类
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){

        Integer type = category.getType();
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(type !=null,Category::getType, type);

        //按sort字段值排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }
}
