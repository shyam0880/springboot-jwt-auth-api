package com.learning.auth.service;

import com.learning.auth.entity.User;

public interface UserService {
	public String userRegister(User user);
	String loginUser(String username, String password);

}
