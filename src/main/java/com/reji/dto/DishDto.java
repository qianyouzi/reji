package com.reji.dto;


import com.reji.bean.DishFlavor;
import lombok.Data;
import java.util.List;

@Data
public class DishDto extends com.reji.bean.Dish {

    private List<DishFlavor> flavors;

    private String categoryName;

    private Integer copies;
}
