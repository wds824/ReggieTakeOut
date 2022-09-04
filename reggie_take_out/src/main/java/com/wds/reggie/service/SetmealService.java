package com.wds.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wds.reggie.dto.SetmealDto;
import com.wds.reggie.entity.Setmeal;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-22 20:46
 */
public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto dto);
}
