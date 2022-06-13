package com.reji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reji.bean.DishFlavor;
import com.reji.dao.DishFlavorDao;
import com.reji.service.DishFlavorService;
import com.reji.service.DishService;
import org.springframework.stereotype.Service;

/**
 * 菜品口味
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorDao, DishFlavor> implements DishFlavorService {
}
