package com.milloc.security.interceptor;

import com.milloc.security.dto.CurrentUser;
import com.milloc.security.exception.UnAuthenticationException;
import com.milloc.security.service.UserService;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
public class LoginInterceptor implements HandlerInterceptor {
    private final UserService userService;

    public LoginInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        CurrentUser currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UnAuthenticationException();
        }
        return true;
    }
}
