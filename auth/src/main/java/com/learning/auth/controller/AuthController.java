package com.learning.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.auth.dto.LoginResponse;
import com.learning.auth.dto.UserDTO;
import com.learning.auth.entity.User;
import com.learning.auth.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	UserService userService;

	@PostMapping("/register")
	public String register(@RequestBody User user) {
	    String responseMessage = userService.userRegister(user);
	    return responseMessage;
	}
	
    @PostMapping("/login")
    public LoginResponse loginUser(@RequestBody UserDTO userDTO) {
        return userService.loginUser(userDTO.getUsername(), userDTO.getPassword());
    }


}
