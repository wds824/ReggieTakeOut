package com.wds.reggie.dto;

import com.wds.reggie.entity.Dish;
import com.wds.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-29 16:20
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
