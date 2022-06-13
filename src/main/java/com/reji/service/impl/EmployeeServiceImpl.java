package com.reji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reji.bean.Employee;
import com.reji.dao.EmployeeDao;
import com.reji.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 继承serviceimpl顶级类,第一个参数写dao,第二个类写实体类
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao, Employee> implements EmployeeService {

}
