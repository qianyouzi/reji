package com.reji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reji.bean.R;
import com.reji.bean.ShoppingCart;
import com.reji.service.ShoppingCartService;
import com.reji.utils.BaseContext;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 手机端页面展示
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 查询购物车列表
     */
    @GetMapping("/list")
    public R list() {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(userId!=null,ShoppingCart::getUserId,userId);
        shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 加入购物车
     */
    @PostMapping("/add")
    public R add(@RequestBody ShoppingCart shoppingCart) {
        //获取查看购物车的用户id和菜品id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> shp = new LambdaQueryWrapper<>();
        //根据用户id查询该用户的购物车
        shp.eq(ShoppingCart::getUserId, userId);
        //判断菜品id是不是空,如果是空添加套餐,不是空添加菜品
        if (dishId == null) {
            shp.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        } else {
            shp.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }
        //根据条件获取用户的购物车
        ShoppingCart one = shoppingCartService.getOne(shp);
        //判断用户购物车该菜品是不是空,是空的话设置数量为1,不是的话加1
        if (one != null) {
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("clean")
    public R delete() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return R.success("删除成功");
    }

    /**
     * 删除单个商品
     */
    @PostMapping("/sub")
    public R deleteById(@RequestBody ShoppingCart shoppingCart){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        Long dishId = shoppingCart.getDishId();
        if (dishId==null){
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }else {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }
        ShoppingCart one = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        Integer number = one.getNumber();
        if (number>1){
            one.setNumber(number-1);
            shoppingCartService.updateById(one);
            return R.success(one);
        }else {
           shoppingCartService.removeById(one.getId());
        }
        return R.success(shoppingCart);
    }
}
