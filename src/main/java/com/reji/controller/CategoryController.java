package com.reji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reji.bean.Category;
import com.reji.bean.R;
import com.reji.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryService;

    /**
     * 增加分类
     */
    @CacheEvict(value = "Category",allEntries = true)
    @PostMapping
    public R add(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("增加分类成功");
    }

    /**
     * 分页查询分类
     */
    @Cacheable(value = "Category",key = "'Category'+#page+'_'+#pageSize")
    @GetMapping("/page")
    public R selectPage(Integer page, Integer pageSize) {
        Page<Category> pg = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        categoryService.page(pg, wrapper);
        return R.success(pg);
    }

    /**
     * 删除分类
     */
    @CacheEvict(value = "Category",allEntries = true)
    @DeleteMapping
    public R deleteById(Long ids) {
        categoryService.remove(ids);
        return R.success("删除分类成功");
    }

    /**
     * 修改分类
     */
    @CacheEvict(value = "Category",allEntries = true)
    @PutMapping
    public R update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     * 获取菜品分类数据
     */
    @Cacheable(value = "Category",key = "'Category'+#category.type")
    @GetMapping("/list")
    public R list(Category category) {
        //设置条件
        LambdaQueryWrapper<Category> cat = new LambdaQueryWrapper<>();
        //按照排序字段排序,排序字段一样按照更新时间排序
        cat.eq(category.getType() != null, Category::getType, category.getType());
        cat.orderByAsc(Category::getSort, Category::getUpdateTime);
        List<Category> list = categoryService.list(cat);
        return R.success(list);
    }
}
