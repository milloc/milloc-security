package com.milloc.security.exception;

import com.milloc.security.dto.CurrentUser;

import java.lang.reflect.Method;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
public class UnAuthorizationException extends Exception {
    private final CurrentUser currentUser;
    private final Method method;

    public UnAuthorizationException(CurrentUser currentUser, Method method) {
        super(String.format("用户 user{userId=%s, username=%s} 没有权限", currentUser.getUserId(), currentUser.getUsername()));
        this.currentUser = currentUser;
        this.method = method;
    }

    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    public Method getMethod() {
        return method;
    }
}
