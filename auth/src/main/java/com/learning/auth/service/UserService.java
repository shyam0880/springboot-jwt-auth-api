package com.learning.auth.service;

import java.util.List;

import com.learning.auth.entity.User;

public interface UserService {
	public String userRegister(User user);
	String loginUser(String username, String password);
	List<User> getUserList();

}
