package com.example.location.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*; 

import com.example.location.service.UserService;
import com.example.location.entity.UserEntity;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public boolean createUser(@RequestBody UserEntity user) {
        return userService.createUser(user);
    }
    
}
