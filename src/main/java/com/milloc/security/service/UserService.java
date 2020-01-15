package com.milloc.security.service;

import com.milloc.security.dto.CurrentUser;
import com.milloc.security.entity.RightEntity;
import com.milloc.security.entity.RoleEntity;
import com.milloc.security.entity.UserEntity;
import lombok.SneakyThrows;

import java.util.Set;

public interface UserService {
    String addUser(UserEntity userEntity);

    String addRole(RoleEntity roleEntity);

    @SneakyThrows
    String addRight(RightEntity rightEntity);

    UserEntity getUserById(String id);

    Set<String> listRights(UserEntity userEntity);

    CurrentUser getCurrentUser();
}
