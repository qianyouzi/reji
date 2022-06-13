package com.reji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reji.bean.Dish;
import com.reji.dto.DishDto;

import java.util.List;

/**
 * 菜品管理
 */

public interface DishService extends IService<Dish> {

    //添加菜品信息和对应口味
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应口味
    DishDto getByIdWithFlavor(Long id);

    //修改菜品信息和对应口味
    void updateWithFlaavor(DishDto dishDto);

    void deleteWithFlaavor(List<Long> ids);
}
