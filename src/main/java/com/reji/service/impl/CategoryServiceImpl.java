package com.reji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reji.bean.Category;
import com.reji.bean.Dish;
import com.reji.bean.Setmeal;
import com.reji.dao.CategoryDao;
import com.reji.err.CustomException;
import com.reji.service.CategoryService;
import com.reji.service.DishService;
import com.reji.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 操作菜品表
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dis = new LambdaQueryWrapper<>();
        dis.eq(Dish::getCategoryId,id);
        int count = dishService.count(dis);
        if (count>0){
            throw new CustomException("当前分类下关联了菜品,不能删除");
        }
        LambdaQueryWrapper<Setmeal> set = new LambdaQueryWrapper<>();
        set.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(set);
        if (count1>0){
            throw new CustomException("当前分类下关联了套餐,不能删除");
        }
        super.removeById(id);
    }
}
