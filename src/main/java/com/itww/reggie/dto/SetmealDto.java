package com.itww.reggie.dto;


import com.itww.reggie.entity.Setmeal;
import com.itww.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
