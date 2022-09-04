package com.wds.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wds.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-13 18:14
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
