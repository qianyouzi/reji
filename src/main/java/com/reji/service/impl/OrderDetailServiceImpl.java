package com.reji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reji.bean.OrderDetail;
import com.reji.dao.OrderDetailDao;
import com.reji.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * 订单明细
 */

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService {
}
