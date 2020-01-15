package com.milloc.security.config;

import com.milloc.security.dto.Res;
import com.milloc.security.exception.UnAuthenticationException;
import com.milloc.security.exception.UnAuthorizationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
@RestController
@ControllerAdvice
@Log4j2
public class ControllerConfig {
    @ExceptionHandler(Exception.class)
    public Res exception(Exception e) {
        log.error(e);
        return Res.err(e.getMessage());
    }

    @ExceptionHandler(UnAuthorizationException.class)
    public Res unAuthorizationException(UnAuthorizationException e) {
        log.error("权限错误 current={} method={}", e.getCurrentUser(), e.getMethod().toString());
        return Res.err(e.getMessage());
    }

    @ExceptionHandler(UnAuthenticationException.class)
    public Res unAuthenticationException(UnAuthenticationException e) {
        log.error("没有登陆", e);
        return Res.err(e.getMessage());
    }
}
