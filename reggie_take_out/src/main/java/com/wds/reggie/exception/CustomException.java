package com.wds.reggie.exception;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-23 11:21
 */
public class CustomException extends RuntimeException{
    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
    }
}
