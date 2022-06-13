package com.reji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reji.bean.Dish;
import com.reji.bean.DishFlavor;
import com.reji.bean.SetmealDish;
import com.reji.dao.DishDao;
import com.reji.dto.DishDto;
import com.reji.err.CustomException;
import com.reji.service.DishFlavorService;
import com.reji.service.DishService;
import com.reji.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;


    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
        this.save(dishDto);
        //获取菜品id
        Long dishId = dishDto.getId();
        //想
        List<DishFlavor> flavors = dishDto.getFlavors();
        //通过流设置菜品口味的id
        List<DishFlavor> collect = flavors.stream().map(item -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //批量保存菜品口味数据
        dishFlavorService.saveBatch(collect);
    }

    /**
     * 查看单个菜品口味
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> df = new LambdaQueryWrapper<>();
        df.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(df);
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 修改菜品口味
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateWithFlaavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);
        //清理当前菜品口味对应数据
        LambdaQueryWrapper<DishFlavor> df = new LambdaQueryWrapper<>();
        df.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(df);
        //获取当前菜品口味,通过stream流将菜品口味重新添加到表中
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map(item -> {
            item.setId(null);
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    @Override
    public void deleteWithFlaavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> dish = new LambdaQueryWrapper<>();
        dish.eq(Dish::getStatus,1);
        dish.in(Dish::getId,ids);
        int count = this.count(dish);
        if (count>0){
            throw new CustomException("菜品正在售卖中,无法删除");
        }
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        int count1 = setmealDishService.count(setmealDishLambdaQueryWrapper);
        if (count1>0){
            throw new CustomException("菜品已绑定套餐,无法删除");
        }
        this.removeByIds(ids);
        LambdaQueryWrapper<DishFlavor> df = new LambdaQueryWrapper<>();
        df.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(df);
    }
}
