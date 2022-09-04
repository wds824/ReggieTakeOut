package com.wds.reggie.common;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-20 8:39
 *
 * 基于ThreadLocal 存取 Id
 *
 */
public class BaseContext {
    private static ThreadLocal<Long> local = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        local.set(id);
    }
    public static Long getCurrentId(){
        return local.get();
    }

}
