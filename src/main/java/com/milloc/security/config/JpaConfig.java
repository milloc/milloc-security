package com.milloc.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.milloc.security.repository")
public class JpaConfig {
}
