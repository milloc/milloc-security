package com.milloc.security.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milloc.security.annotation.RightControl;
import com.milloc.security.dto.CurrentUser;
import com.milloc.security.exception.UnAuthorizationException;
import com.milloc.security.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
@Log4j2
public class RightControlInterceptor implements HandlerInterceptor {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public RightControlInterceptor(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Class<?> controller = handlerMethod.getBeanType();
        CurrentUser currentUser = userService.getCurrentUser();

        if (controller.getAnnotation(RightControl.class) != null) {
            if (!currentUser.getRights().contains(controller.toString())) {
                unAuthorization(currentUser, handlerMethod.getMethod());
                return false;
            }
        }

        if (handlerMethod.getMethodAnnotation(RightControl.class) != null) {
            if (!currentUser.getRights().contains(handlerMethod.getMethod().toString())) {
                unAuthorization(currentUser, handlerMethod.getMethod());
                return false;
            }
        }

        return true;
    }

    @SneakyThrows
    private void unAuthorization(CurrentUser user, Method method) {
        log.debug("没有权限 user = {}, method = {}", objectMapper.writeValueAsString(user), method.toString());
        throw new UnAuthorizationException(user, method);
    }
}
