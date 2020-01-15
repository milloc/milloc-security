package com.milloc.security;

import com.milloc.security.entity.RightEntity;
import com.milloc.security.entity.RoleEntity;
import com.milloc.security.entity.UserEntity;
import com.milloc.security.repository.RightRepository;
import com.milloc.security.repository.RoleRepository;
import com.milloc.security.repository.UserRepository;
import com.milloc.security.service.RightControlService;
import com.milloc.security.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;

@SpringBootTest(classes = SecurityApplication.class)
@Log4j2
class SecurityApplicationTests {

    @Autowired
    private RightControlService rightControlService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RightRepository rightRepository;

    @Test
    @Transactional
    @Rollback(false)
    void addRights() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("ming");
        UserEntity entity = userRepository.findOne(Example.of(userEntity)).get();

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("admin");
        roleEntity.setDescription("test admin");
        RoleEntity admin = roleRepository.save(roleEntity);

        entity.setRoles(new ArrayList<>(Collections.singletonList(admin)));
        userRepository.saveAndFlush(entity);

        RightEntity rightEntity = new RightEntity();
        rightEntity.setName("test_controller");
        rightEntity.setContent("com.milloc.security.controller.TestController");
        RightEntity r1 = rightRepository.save(rightEntity);

        admin.setRights(new ArrayList<>(Collections.singletonList(r1)));
        roleRepository.saveAndFlush(admin);
    }

}
