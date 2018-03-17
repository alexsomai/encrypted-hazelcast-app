package com.alexsomai.blog.controller;


import com.alexsomai.blog.model.UserInfo;
import com.alexsomai.blog.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping(value = "/{accountId}", method = RequestMethod.GET)
    public UserInfo getUserInfo(@PathVariable long accountId) {
        return userInfoService.getUserInfo(accountId);
    }
}
