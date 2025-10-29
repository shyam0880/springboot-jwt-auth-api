package com.learning.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.learning.auth.entity.User;
import com.learning.auth.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
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
		try {
	        Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(username, password)
	        );

	        SecurityContextHolder.getContext().setAuthentication(authentication);

	        return "Login Successful";
	    } catch (BadCredentialsException e) {
	        return "Incorrect Username or Password";
	    } catch (UsernameNotFoundException e) {
	        return "User Not Found";
	    } catch (Exception e) {
	        return "Authentication Failed: " + e.getMessage();
	    }
	}

	@Override
	public List<User> getUserList() {
		List<User> users = userRepository.findAll();
		return users;
	}

}
