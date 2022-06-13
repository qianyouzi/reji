package com.reji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reji.bean.*;
import com.reji.dto.SetmealDto;
import com.reji.service.CategoryService;
import com.reji.service.DishService;
import com.reji.service.SetmealDishService;
import com.reji.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 套餐管理
 */
@Slf4j
@RequestMapping("/setmeal")
@RestController
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R add(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     */
    @GetMapping("/page")
    public R page(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> sq = new LambdaQueryWrapper<>();
        sq.like(StringUtils.isNotEmpty(name), Setmeal::getName, name).
                orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, sq);
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> collect = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if (byId != null) {
                setmealDto.setCategoryName(byId.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(collect);
        return R.success(setmealDtoPage);
    }

    /**
     * 套餐批量和单个删除
     */
    @DeleteMapping
    public R delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }

    /**
     * 查询套餐
     */
    @GetMapping("/list")
    public R list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> set = new LambdaQueryWrapper<>();
        set.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        set.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> list = setmealService.list(set);
        return R.success(list);
    }

    /**
     * 套餐批量起售
     * 套餐批量停售
     */
    @PostMapping("/status/{a}")
    public R status(@PathVariable Integer a,@RequestParam List<Long> ids){
        LambdaUpdateWrapper<Setmeal> setmealLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        setmealLambdaUpdateWrapper.in(Setmeal::getId,ids).set(Setmeal::getStatus,a).set(Setmeal::getUpdateTime, LocalDateTime.now());
        setmealService.update(setmealLambdaUpdateWrapper);
        return R.success("售卖状态修改成功");
    }

    /**
     * 回显套餐数据
     */
    @GetMapping("/{id}")
    public R myGetId(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.myGetId(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     */
    @PutMapping
    public R update(@RequestBody SetmealDto setmealDto){
        setmealService.updateSetmeal(setmealDto);
        return R.success("修改成功");
    }

    /**
     * 用户端查看套餐详情
     */
    @GetMapping("/dish/{id}")
    public R getId(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);
        List<SetmealDish> collect = list.stream().map(item -> {
            Long dishId = item.getDishId();
            Dish byId = dishService.getById(dishId);
            item.setImage(byId.getImage());
            return item;
        }).collect(Collectors.toList());
        return R.success(collect);
    }
}
