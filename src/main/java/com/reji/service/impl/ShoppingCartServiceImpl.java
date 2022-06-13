package com.reji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reji.bean.ShoppingCart;
import com.reji.dao.ShoppingCartDao;
import com.reji.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author 74545
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartDao, ShoppingCart> implements ShoppingCartService {
}
