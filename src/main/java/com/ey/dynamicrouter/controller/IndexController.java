package com.ey.dynamicrouter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @RequestMapping(value = "/error")
    public String error(@RequestParam(required = false, defaultValue = "未知错误") String message) {
        return message;
    }
}
