package com.learning.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String getGreeting(){
        return "Hello! Good Morning.";
    }
    
    @GetMapping("/security_details")
    public String securityDetails() {
    	return "File contains security details.";
    }
}
