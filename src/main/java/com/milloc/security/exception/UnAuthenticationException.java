package com.milloc.security.exception;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
public class UnAuthenticationException extends Exception {
    public UnAuthenticationException() {
        super("没有登陆");
    }
}
