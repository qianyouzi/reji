package com.reji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reji.bean.Employee;
import com.reji.bean.R;
import com.reji.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


/**
 * 员工操作管理
 */
@Slf4j
@RequestMapping("/employee")
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeService;

    //管理端登录
    @PostMapping("/login")
    public R login(@RequestBody Employee emp, HttpSession session) {
        //接收用户登录的用户名和密码 大括号表示占位符
        String password = DigestUtils.md5DigestAsHex(emp.getPassword().getBytes());
        //根据用户名查询数据库
        LambdaQueryWrapper<Employee> lw = new LambdaQueryWrapper<>();
        lw.eq(Employee::getUsername, emp.getUsername());
        Employee employee = employeeService.getOne(lw);
        //判断用户名是否存在
        if (employee == null) {
            return R.error("员工不存在");
        }
        //比对密码是否一样
        if (password == null || !employee.getPassword().equals(password)) {
            return R.error("账号或者密码不匹配");
        }
        //判断员工状态是否禁用
        if (employee.getStatus() == 0) {
            return R.error("员工已禁用");
        }
        //将员工id存入session域对象中
        session.setAttribute("employee", employee.getId());
        return R.success(employee);
    }

    //管理端退出登录
    @PostMapping("/logout")
    public R logout(HttpSession session) {
        session.invalidate();
        return R.success("退出成功");
    }

    //管理端添加员工
    @CacheEvict(value = "employee",allEntries = true)
    @PostMapping
    public R add(@RequestBody Employee employee, HttpSession session) {
        employee.setStatus(1);
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //把数据添加入数据库中
        boolean flag = employeeService.save(employee);
        //返回结果
        return flag ? R.success("增加成功") : R.error("增加失败");
    }

    //分页查询显示所有员工
    @Cacheable(value = "employee",key = "#page+'_'+#pageSize+'_'+#name")
    @GetMapping("/page")
    public R selectPage(Integer page, Integer pageSize, String name) {
        Page<Employee> pg = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> eqw = new LambdaQueryWrapper<>();
        eqw.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        eqw.orderByDesc(Employee::getCreateTime);
        employeeService.page(pg, eqw);
        return R.success(pg);
    }

    //修改员工状态
    @CacheEvict(value = "employee",allEntries = true)
    @PutMapping
    public R update(HttpSession session,@RequestBody Employee employee){
        log.info(employee.toString());
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    //查询单个员工信息
    @GetMapping("/{id}")
    public R updateById(@PathVariable Long id){
        Employee byId = employeeService.getById(id);
        return R.success(byId);
    }
}
