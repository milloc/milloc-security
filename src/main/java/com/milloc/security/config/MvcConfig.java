package com.milloc.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milloc.security.interceptor.LoginInterceptor;
import com.milloc.security.interceptor.RightControlInterceptor;
import com.milloc.security.service.RightControlService;
import com.milloc.security.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public MvcConfig(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(userService))
                .addPathPatterns("/**").
                excludePathPatterns("/user/login", "/user/register");
        registry.addInterceptor(new RightControlInterceptor(userService, objectMapper))
                .addPathPatterns("/**").excludePathPatterns("/user/**");
    }
}
