package com.reji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reji.bean.User;
import com.reji.dao.UserDao;
import com.reji.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户管理
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

}
