package com.wds.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wds.reggie.entity.User;
import com.wds.reggie.mapper.UserMapper;
import com.wds.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-09-02 9:03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
