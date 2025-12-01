package com.learning.auth.service;

import java.util.List;

import com.learning.auth.dto.LoginResponse;
import com.learning.auth.dto.UserDTO;
import com.learning.auth.entity.User;

public interface UserService {
	public String userRegister(User user);
	LoginResponse loginUser(String username, String password);
	List<UserDTO> getUserList();

}
