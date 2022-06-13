package com.reji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reji.bean.Setmeal;
import com.reji.bean.SetmealDish;
import com.reji.dao.SetmealDao;
import com.reji.dto.SetmealDto;
import com.reji.err.CustomException;
import com.reji.service.SetmealDishService;
import com.reji.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 *
 * @author 74545
 */
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealDao, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 增加套餐
     *
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐
     *
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态是否可用
        LambdaQueryWrapper<Setmeal> sq = new LambdaQueryWrapper<>();
        sq.eq(Setmeal::getStatus, 1);
        sq.in(Setmeal::getId, ids);
        int count = this.count(sq);
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果不可用，就删除
        this.removeByIds(ids);
        //设置套餐关系表的删除条件,条件为套餐关系表的dishid等于传入的ids
        LambdaQueryWrapper<SetmealDish> sd = new LambdaQueryWrapper<>();
        sd.in(SetmealDish::getDishId, ids);
        setmealDishService.remove(sd);
    }

    /**
     * 回显套餐数据
     */
    @Override
    public SetmealDto myGetId(Long id) {
        Setmeal byId = this.getById(id);
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(wrapper);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(byId, setmealDto);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    /**
     * 修改套餐
     */
    @Transactional
    @Override
    public void updateSetmeal(SetmealDto setmealDto) {
        //更新套餐表
        this.updateById(setmealDto);
        //删除套餐内原来的菜品
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        //把新的菜品添加进去
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> collect = setmealDishes.stream().map(item -> {
            item.setId(null);
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(collect);
    }
}
