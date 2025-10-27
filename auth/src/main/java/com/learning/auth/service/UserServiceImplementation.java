package com.learning.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.learning.auth.entity.User;
import com.learning.auth.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Override
	public String userRegister(User user) {
		if(userRepository.findByUsername(user.getUsername()).isPresent()) return "User Already Present";
		user.setPassword(encoder.encode(user.getPassword()));
		userRepository.save(user);
		return "User Registered Successfully!";
	}

	@Override
	public String loginUser(String username, String password) {
		Optional<User> optionalUser = userRepository.findByUsername(username);
	    if (optionalUser.isEmpty()) return "User Not Present";

	    User user = optionalUser.get();

	    if (encoder.matches(password, user.getPassword())) {
	        return "Login Successful";
	    } else {
	        return "Incorrect Password";
	    }
	}

}
