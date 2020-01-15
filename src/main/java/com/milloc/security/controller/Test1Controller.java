package com.milloc.security.controller;

import com.milloc.security.annotation.RightControl;
import com.milloc.security.dto.Res;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
@RestController
@RequestMapping("/test1")
public class Test1Controller {
    @RequestMapping("/test")
    @RightControl
    public Res<String> test() {
        return Res.ok("hello");
    }
}
