package com.wds.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wds.reggie.common.R;
import com.wds.reggie.entity.User;
import com.wds.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-09-02 9:04
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public R<String> login(@RequestBody User user, HttpServletRequest request) {
        log.info("User login :{}", user.getPhone());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, user.getPhone());
        User dbUser = userService.getOne(queryWrapper);
        if (dbUser == null) {
            User newUser = new User();
            newUser.setPhone(user.getPhone());
            newUser.setName("newUser");
            newUser.setSex("男");
            newUser.setStatus(1);
            newUser.setIdNumber("111222333444555666");
            newUser.setAvatar("null");

            userService.save(newUser);
            dbUser = newUser;
        }
        request.getSession().setAttribute("userId", dbUser.getId());
        log.info("user login id: {}", dbUser.getId());

        return R.success("登录成功!");
    }

    @PostMapping("/loginout")
    public R<String> loginOut(HttpSession session) {
        session.removeAttribute("userId");
        return R.success("User Login Out");
    }
}
