package com.reji.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reji.bean.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 *  操作员工表
 * @author 74545
 */

public interface EmployeeDao extends BaseMapper<Employee> {
}
