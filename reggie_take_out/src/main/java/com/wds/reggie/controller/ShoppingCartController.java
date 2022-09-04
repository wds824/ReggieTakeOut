package com.wds.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wds.reggie.common.BaseContext;
import com.wds.reggie.common.R;
import com.wds.reggie.entity.ShoppingCart;
import com.wds.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-09-03 14:35
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<String> save(@RequestBody ShoppingCart cart) {

        // set userid
        Long id = BaseContext.getCurrentId();
        log.info("user:{} add dish to cart", id);

        cart.setUserId(id);

        if (cart.getDishId() != null) {
            //添加的菜品
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getDishId, cart.getDishId());
            queryWrapper.eq(ShoppingCart::getUserId, id);
            if (setNumber(queryWrapper))
                return R.success("添加成功");

        } else {
            //添加的套餐
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getSetmealId, cart.getSetmealId());
            if (setNumber(queryWrapper))
                return R.success("添加成功");
        }
        //没有已存在的记录，插入新的
        cart.setNumber(1);
        cart.setCreateTime(LocalDateTime.now());
        shoppingCartService.save(cart);
        return R.success("添加成功");
    }

    /**
     * 抽取增加单个订单的数量的方法
     */
    private boolean setNumber(LambdaQueryWrapper<ShoppingCart> queryWrapper) {
        ShoppingCart cart1 = shoppingCartService.getOne(queryWrapper);

        if (cart1 != null) {
//                number++
            LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ShoppingCart::getId, cart1.getId());
            updateWrapper.set(ShoppingCart::getNumber, 1 + (cart1.getNumber()));
            shoppingCartService.update(updateWrapper);
            return true;
        }
        return false;
    }

    /**
     * 展示购物车列表
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
//        当前登录用户的ID
        Long userId = BaseContext.getCurrentId();
        log.info("user:{} show shopping cart", userId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> cartList = shoppingCartService.list(queryWrapper);

        return R.success(cartList);
    }

    /**
     *  清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        Long id = BaseContext.getCurrentId();
        log.info("user:{} clean shopping cart", id);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, id);
        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功。");
    }
}
