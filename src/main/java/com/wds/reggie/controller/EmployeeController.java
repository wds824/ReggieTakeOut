package com.wds.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wds.reggie.common.BaseContext;
import com.wds.reggie.common.R;
import com.wds.reggie.entity.Employee;
import com.wds.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-13 18:19
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录模块
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if (emp == null) {
            return R.error("用户名密码错误");
        }

        if (!password.equals(emp.getPassword())) {
            return R.error("用户名密码错误");
        }

        if (emp.getStatus() != 1) {
            return R.error("账号状态异常");
        }

        request.getSession().setAttribute("employeeId", emp.getId());
        log.info("employee login id: {}", emp.getId());
        emp.setPassword("");
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employeeId");
        return R.success("退出成功");
    }

    /**
     * 添加员工信息
     * 默认密码为用户名
     */
    @PostMapping()
    public R<String> addEmployee(HttpServletRequest request, @RequestBody Employee employee) {
        String password = "123456";

        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        employee.setPassword(password);

//        LocalDateTime now = LocalDateTime.now();
//        employee.setCreateTime(now);
//        employee.setUpdateTime(now);

//        Long id = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);

        employeeService.save(employee);

        return R.success("添加成功！");

    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page<Employee>> pagination(int page, int pageSize, String name) {

        Page<Employee> pageData = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageData, queryWrapper);
        return R.success(pageData);
    }

    /**
     * 修改员工信息
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
//        更新修改的时间
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(updateUserId);
//        Long updateUserId = (Long) request.getSession().getAttribute("employee");
//        更新数据
        employeeService.updateById(employee);



        return R.success("操作成功!");
    }

    /**
     * 员工信息回显
     */
    @GetMapping("/{id}")
    public R<Employee> reShow(@PathVariable Long id) {

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getId, id);
        Employee employee = employeeService.getOne(queryWrapper);
        if (employee == null) {
            return R.error("没有查询到对应员工信息。");
        }
        return R.success(employee);
    }
}
