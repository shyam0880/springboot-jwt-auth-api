package com.learning.auth.dto;

public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private String role;
    
	public UserDTO() {
		super();
	}
	
	public UserDTO(Long id, String username, String role) {
		super();
		this.id = id;
		this.username = username;
		this.role = role;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", username=" + username + ", role=" + role + "]";
	}
}
