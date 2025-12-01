package com.learning.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.auth.dto.UserDTO;
import com.learning.auth.entity.User;
import com.learning.auth.service.UserServiceImplementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/userController")
public class UserController {
	
	@Autowired
	private UserServiceImplementation userServiceImplementation;
	
	@GetMapping("/get_user")
	public List<UserDTO> getUserDetails() {
		return userServiceImplementation.getUserList();
	}
	

}
