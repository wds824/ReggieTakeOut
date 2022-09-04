package com.wds.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wds.reggie.entity.Employee;
import com.wds.reggie.mapper.EmployeeMapper;
import com.wds.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-13 18:13
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
