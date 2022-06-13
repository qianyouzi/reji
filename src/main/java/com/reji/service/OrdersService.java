package com.reji.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reji.bean.Orders;
import com.reji.dto.DishDto;
import com.reji.dto.OrdersDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单处理
 */

public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);

    /**
     * 用户个人中心
     */
    Page<OrdersDto> ordersDtoPage(Integer page, Integer pageSize);

    /**
     * 用户再来一单
     */
    void again(Orders orders);
}
