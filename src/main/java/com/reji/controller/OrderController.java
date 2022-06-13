package com.reji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reji.bean.OrderDetail;
import com.reji.bean.Orders;
import com.reji.bean.R;
import com.reji.dto.DishDto;
import com.reji.dto.OrdersDto;
import com.reji.service.OrderDetailService;
import com.reji.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * 订单处理
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 提交订单
     */
    @PostMapping("/submit")
    public R submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 后台管理系统查看订单
     * 分页,分时段查询订单
     */
    @Transactional
    @GetMapping("/page")
    public R page(Integer page, Integer pageSize, String beginTime, String endTime, String number) {
        LocalDateTime begin = null;
        LocalDateTime end = null;
        if (beginTime != null && endTime != null) {
            begin = LocalDateTime.parse(beginTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            end = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        OrderDetail orderDetail = new OrderDetail();
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.between(beginTime != null && endTime != null, Orders::getOrderTime, begin, end);
        ordersLambdaQueryWrapper.like(number != null, Orders::getNumber, number);
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(ordersPage, ordersLambdaQueryWrapper);
        return R.success(ordersPage);
    }

    /**
     * 用户个人中心
     */
    @GetMapping("/userPage")
    public R userPage(Integer page, Integer pageSize) {
        Page<OrdersDto> dishDto = ordersService.ordersDtoPage(page, pageSize);
        return R.success(dishDto);
    }

    /**
     * 修改订单状态
     */
    @PutMapping
    public R update(@RequestBody Orders orders) {
        LambdaUpdateWrapper<Orders> ordersLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        ordersLambdaUpdateWrapper.eq(Orders::getId, orders.getId()).set(Orders::getStatus, orders.getStatus());
        ordersService.update(ordersLambdaUpdateWrapper);
        return R.success("修改成功");
    }

    /**
     * 用户再来一单
     */
    @PostMapping("/again")
    public R again(@RequestBody Orders orders) {
        ordersService.again(orders);
        return R.success("欢迎继续购买");
    }
}
