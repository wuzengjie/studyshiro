package com.example.ssoserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @RequestMapping("login")
    public void login(HttpServletRequest request){
        System.out.println("测试");
    }
}
