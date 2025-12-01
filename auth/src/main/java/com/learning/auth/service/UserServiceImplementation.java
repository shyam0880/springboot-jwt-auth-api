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

import com.learning.auth.dto.LoginResponse;
import com.learning.auth.dto.UserDTO;
import com.learning.auth.entity.User;
import com.learning.auth.repository.UserRepository;
import com.learning.auth.util.JwtUtil;

@Service
public class UserServiceImplementation implements UserService {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Override
	public String userRegister(User user) {
		if(userRepository.findByUsername(user.getUsername()).isPresent()) return "User Already Present";
		user.setPassword(encoder.encode(user.getPassword()));
		userRepository.save(user);
		return "User Registered Successfully!";
	}

	@Override
	public LoginResponse loginUser(String username, String password) {
		try {
	        Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(username, password)
	        );

	        SecurityContextHolder.getContext().setAuthentication(authentication);

	        // Generate and return JWT token
	        String token = jwtUtil.generateToken(username);
	        return new LoginResponse(token, "Login Successful");
	    } catch (BadCredentialsException e) {
	        return new LoginResponse(null, "Incorrect Username or Password");
	    } catch (UsernameNotFoundException e) {
	        return new LoginResponse(null, "User Not Found");
	    } catch (Exception e) {
	        return new LoginResponse(null, "Authentication Failed: " + e.getMessage());
	    }
	}

	@Override
	public List<UserDTO> getUserList() {
		return userRepository.findAll().stream().map(x->userMapping(x)).toList();
	}
	
	private UserDTO userMapping(User user) {
		return new UserDTO(user.getId(), user.getUsername(), user.getRole());
	
	}
	

}
