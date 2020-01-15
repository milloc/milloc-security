package com.milloc.security.dto;

import lombok.Data;

import java.util.Set;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
@Data
public class CurrentUser {
    public static final String USER_ID_SESSION_KEY = "user_id";
    public static final String CURRENT_USER_SESSION_KEY = "current_user";

    private String userId;
    private String username;
    private Set<String> rights;
}
