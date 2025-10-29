package com.learning.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.learning.auth.service.UserDetailsServiceImplementation;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private UserDetailsServiceImplementation userDetailsService;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		return http
				.csrf(csrf->csrf.disable())
				.authorizeHttpRequests(auth->auth
						.requestMatchers("/auth/**").permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(withDefaults())  
				.httpBasic(withDefaults())
	            .build();
		
	}
	
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception{
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder())
				.and()
				.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

}
