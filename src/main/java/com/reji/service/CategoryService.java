package com.reji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reji.bean.Category;
/**
 * 操作菜品表
 */
public interface CategoryService extends IService<Category> {

    //根据id删除分类
    public void remove(Long id);
}
