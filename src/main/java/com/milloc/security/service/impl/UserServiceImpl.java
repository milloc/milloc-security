package com.milloc.security.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milloc.security.dto.CurrentUser;
import com.milloc.security.entity.RightEntity;
import com.milloc.security.entity.RoleEntity;
import com.milloc.security.entity.UserEntity;
import com.milloc.security.repository.RightRepository;
import com.milloc.security.repository.RoleRepository;
import com.milloc.security.repository.UserRepository;
import com.milloc.security.service.UserService;
import lombok.SneakyThrows;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RightRepository rightRepository;
    private final RoleRepository roleRepository;
    private final ObjectMapper objectMapper;

    public UserServiceImpl(UserRepository userRepository, RightRepository rightRepository, RoleRepository roleRepository,
                           ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.rightRepository = rightRepository;
        this.roleRepository = roleRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public String addUser(UserEntity userEntity) {
        UserEntity example = new UserEntity();
        example.setUsername(userEntity.getUsername());
        checkArgument(!userRepository.exists(Example.of(example)),
                "username duplicated username = %s", userEntity.getUsername());
        return userRepository.save(userEntity).getId();
    }

    @Override
    public String addRole(RoleEntity roleEntity) {
        RoleEntity example = new RoleEntity();
        example.setName(roleEntity.getName());
        checkArgument(!roleRepository.exists(Example.of(example)),
                "role.name duplicated role.name = %s", roleEntity.getName());
        return roleRepository.save(roleEntity).getId();
    }

    @SneakyThrows
    @Override
    public String addRight(RightEntity rightEntity) {
        RightEntity exampleEntity = new RightEntity();
        exampleEntity.setName(rightEntity.getName());
        exampleEntity.setContent(rightEntity.getContent());
        Example<RightEntity> example = Example.of(exampleEntity, ExampleMatcher.matchingAny());
        checkArgument(!rightRepository.exists(example),
                "right duplicated right = %s", objectMapper.writeValueAsString(rightEntity));
        return rightRepository.save(rightEntity).getId();
    }

    @Override
    public UserEntity getUserById(String id) {
        return userRepository.getOne(id);
    }

    @Override
    public Set<String> listRights(UserEntity userEntity) {
        List<RoleEntity> roles = userEntity.getRoles();
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(RoleEntity::getRights)
                .flatMap(List::stream)
                .map(RightEntity::getContent)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public CurrentUser getCurrentUser() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();

        HttpSession session = request.getSession();
        CurrentUser currentUser = (CurrentUser) session.getAttribute(CurrentUser.CURRENT_USER_SESSION_KEY);
        if (currentUser == null) {
            String userId = (String) session.getAttribute(CurrentUser.USER_ID_SESSION_KEY);
            if (userId == null) {
                return null;
            }

            UserEntity user = getUserById(userId);
            if (user == null) {
                session.invalidate();
                return null;
            }

            currentUser = new CurrentUser();
            currentUser.setUserId(user.getId());
            currentUser.setUsername(user.getUsername());
            currentUser.setRights(listRights(user));

            session.setAttribute(CurrentUser.CURRENT_USER_SESSION_KEY, currentUser);
        }

        return currentUser;
    }
}
