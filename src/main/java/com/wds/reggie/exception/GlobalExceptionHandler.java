package com.wds.reggie.exception;

import com.wds.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-15 16:55
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> sqlExceptionHandler(SQLIntegrityConstraintViolationException e) {
        log.error(e.getMessage());
        if (e.getMessage().contains("Duplicate entry")) {
            String[] split = e.getMessage().split(" ");
            return R.error(split[2] + " 已存在。");
        }
        return R.error("未知错误!");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> CustomExceptionHandler(CustomException e) {
        log.error(e.getMessage());
        return R.error(e.getMessage());
    }
}
