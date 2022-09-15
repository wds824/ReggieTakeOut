package com.wds.reggie.common.utils;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-20 8:39
 * <p>
 * 基于ThreadLocal 存取 Id
 */
public class BaseContext {
    private static final ThreadLocal<CurrentUser> local = new ThreadLocal<>();

    public static CurrentUser getCurrentId() {
        if (local.get() == null) {
            local.set(new CurrentUser());
        }

        return local.get();
    }

}
