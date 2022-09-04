package com.wds.reggie.dto;

import com.wds.reggie.entity.Setmeal;
import com.wds.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
