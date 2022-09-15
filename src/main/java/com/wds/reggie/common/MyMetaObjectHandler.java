package com.wds.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.wds.reggie.common.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-19 14:23
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    //    公共字段自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        Long currentId = BaseContext.getCurrentId().getEmployeeId();

        metaObject.setValue("createUser", currentId);
        metaObject.setValue("updateUser", currentId);
    }

    //    公共字段自动填充
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());

        Long currentId = BaseContext.getCurrentId().getEmployeeId();
        metaObject.setValue("updateUser", currentId);

    }
}
