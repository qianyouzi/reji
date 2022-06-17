package com.reji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reji.bean.*;
import com.reji.dao.OrdersDao;
import com.reji.dto.DishDto;
import com.reji.dto.OrdersDto;
import com.reji.err.CustomException;
import com.reji.service.*;
import com.reji.utils.BaseContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.element.VariableElement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 订单处理
 */

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, Orders> implements OrdersService {


    @Autowired
    private ShoppingCartService shoppingCartService;  //操作购物车

    @Autowired
    private UserService userService;  //操作用户

    @Autowired
    private AddressBookService addressBookService;  //操作地址

    @Autowired
    private OrderDetailService orderDetailService;  //操作订单明细

    /**
     * 提交订单
     */
    @Transactional
    @Override
    public void submit(Orders orders) {
        //获取当前用户id
        Long userId = BaseContext.getCurrentId();
        //用户购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        if (list == null || list.size() == 0) {
            throw new CustomException("购物车为空,不能下单");
        }
        //用户数据
        User user = userService.getById(userId);
        //获取地址id
        Long addressBookId = orders.getAddressBookId();
        //地址数据
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("用户地址信息有误,不能下单");
        }
        //订单号
        long id = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);

        //组装订单明细信息
        List<OrderDetail> orderDetailList = list.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setId(id);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(id));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getCityName()) +
                (addressBook.getCityName() == null ? "" : addressBook.getCityName()) +
                (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入一条数据
        this.save(orders);
        //向订单明细表插入多条数据
        orderDetailService.saveBatch(orderDetailList);
        shoppingCartService.remove(wrapper);


    }

    /**
     * 用户分页查询订单信息
     */
    @Override
    public Page<OrdersDto> ordersDtoPage(Integer page, Integer pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId, userId);
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        this.page(ordersPage, ordersLambdaQueryWrapper);
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");
        List<Orders> records = ordersPage.getRecords();
        if (records.size()<1){
            List<OrdersDto> ordersDto = new ArrayList<>();
            User byId = userService.getById(userId);
            OrdersDto ordersDto1 = new OrdersDto();
            ordersDto1.setUserName(byId.getName());
            ordersDto1.setOrderDetails(new ArrayList<>());
            ordersDto.add(ordersDto1);
            ordersDtoPage.setRecords(ordersDto);
            return ordersDtoPage;
        }
        List<OrdersDto> collect = records.stream().map(item -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> list = orderDetailService.list(orderDetailLambdaQueryWrapper);
            ordersDto.setOrderDetails(list);
            ordersDto.setConsignee(item.getConsignee());
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(collect);
        return ordersDtoPage;
    }

    /**
     * 用户再来一单
     */
    @Override
    public void again(Orders orders) {
        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,orders.getId());
        List<OrderDetail> list = orderDetailService.list(orderDetailLambdaQueryWrapper);
        if (list.size()>0){
            for (OrderDetail orderDetail : list) {
                Long userId = BaseContext.getCurrentId();
                ShoppingCart shoppingCart = new ShoppingCart();
                shoppingCart.setUserId(userId);
                if (orderDetail.getDishId()!=null){
                    shoppingCart.setDishId(orderDetail.getDishId());
                    shoppingCart.setNumber(orderDetail.getNumber());
                    shoppingCart.setAmount(orderDetail.getAmount());
                    shoppingCart.setImage(orderDetail.getImage());
                }else {
                    shoppingCart.setSetmealId(orderDetail.getSetmealId());
                    shoppingCart.setNumber(orderDetail.getNumber());
                    shoppingCart.setAmount(orderDetail.getAmount());
                    shoppingCart.setImage(orderDetail.getImage());
                }
                shoppingCartService.save(shoppingCart);
            }
        }
    }
}
