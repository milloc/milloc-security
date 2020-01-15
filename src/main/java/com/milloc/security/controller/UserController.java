package com.milloc.security.controller;

import com.milloc.security.dto.Res;
import com.milloc.security.entity.UserEntity;
import com.milloc.security.service.RightControlService;
import com.milloc.security.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.Set;

import static com.milloc.security.dto.CurrentUser.USER_ID_SESSION_KEY;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
@RequestMapping("/user")
@RestController
public class UserController {
    private final UserService userService;
    private final RightControlService rightControlService;

    public UserController(UserService userService, RightControlService rightControlService) {
        this.userService = userService;
        this.rightControlService = rightControlService;
    }

    @PostMapping("/login")
    public Res login(String id, HttpSession session) {
        UserEntity user = userService.getUserById(id);
        Objects.requireNonNull(user, "fail to login");
        session.setAttribute(USER_ID_SESSION_KEY, user.getId());
        return Res.ok();
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Res<String> register(@RequestBody UserEntity userEntity) {
        return Res.ok(userService.addUser(userEntity));
    }

    @GetMapping("/logout")
    public Res logout(HttpSession session) {
        session.invalidate();
        return Res.ok();
    }

    @GetMapping("/rightControls")
    public Res<Set<String>> listRightControls() {
        return Res.ok(rightControlService.listRightControls());
    }
}
