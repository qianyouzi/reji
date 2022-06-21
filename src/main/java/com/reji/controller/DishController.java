package com.reji.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reji.bean.Category;
import com.reji.bean.Dish;
import com.reji.bean.DishFlavor;
import com.reji.bean.R;
import com.reji.dto.DishDto;
import com.reji.service.CategoryService;
import com.reji.service.DishFlavorService;
import com.reji.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private RedisTemplate redisTemplate;

    //新增菜品
    @PostMapping
    public R add(@RequestBody DishDto dishDto) {
        //清理保存菜品的缓存
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    //菜品分类
    @GetMapping("/page")
    public R page(Integer page, Integer pageSize, String name) {
        //分页查询器
        Page<Dish> pag = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //查询条件
        LambdaQueryWrapper<Dish> dish = new LambdaQueryWrapper<>();
        dish.like(name != null, Dish::getName, name);
        dish.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pag, dish);
        //对象拷贝,把pag的数据拷贝到dishdtopage里面,排除records属性
        BeanUtils.copyProperties(pag, dishDtoPage, "records");
        List<Dish> records = pag.getRecords();
        List<DishDto> collect = records.stream().map(item -> {
            DishDto dishDishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDishDto);
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if (byId != null) {
                dishDishDto.setCategoryName(byId.getName());
            }
            return dishDishDto;
        }).collect(Collectors.toList());
        //设置dishDtoPage对象的records属性
        dishDtoPage.setRecords(collect);
        return R.success(dishDtoPage);
    }

    //根据id查询菜品
    @GetMapping("/{id}")
    public R addById(@PathVariable Long id) {
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);
    }

    //修改菜品
    @PutMapping
    public R update(@RequestBody DishDto dishDto) {
        //清理修改菜品的缓存
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        dishService.updateWithFlaavor(dishDto);
        return R.success("修改菜品成功");
    }

    //根据分类id查询菜品列表
    @GetMapping("/list")
    public R list(Dish dish) {
        List<DishDto> dishDtoList = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //从redis数据库中获取指定菜品,如果有就直接返回,没有就执行到最后然后保存到redis数据库中
        String dis = (String) redisTemplate.opsForValue().get(key);
        if (dis != null) {
            dishDtoList = JSON.parseObject(dis, List.class);
            return R.success(dishDtoList);
        }
        //设置条件,查询菜品列表
        LambdaQueryWrapper<Dish> dd = new LambdaQueryWrapper<>();
        dd.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        dd.eq(Dish::getStatus, 1);
        dd.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);
        List<Dish> list = dishService.list(dd);
        dishDtoList = list.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //根据id查询分类对象
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if (byId != null) {
                String name = byId.getName();
                dishDto.setCategoryName(name);
            }
            //根据id查询口味对象
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> df = new LambdaQueryWrapper<>();
            df.eq(DishFlavor::getDishId, id);
            List<DishFlavor> list1 = dishFlavorService.list(df);
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key, JSON.toJSONString(dishDtoList), 60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }

    //删除菜品
    @DeleteMapping
    public R delete(@RequestParam List<Long> ids) {
        dishService.deleteWithFlaavor(ids);
        return R.success("删除成功");
    }

    /**
     * 批量起售,批量停售
     */
    @PostMapping("/status/{a}")
    public R state(@PathVariable Integer a, @RequestParam List<Long> ids) {
        LambdaUpdateWrapper<Dish> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        dishLambdaUpdateWrapper.in(Dish::getId, ids).set(Dish::getStatus, a).set(Dish::getUpdateTime, LocalDateTime.now());
        dishService.update(dishLambdaUpdateWrapper);
        return R.success("修改成功");
    }

}
