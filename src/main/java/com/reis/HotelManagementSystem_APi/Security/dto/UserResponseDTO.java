package com.reis.HotelManagementSystem_APi.Security.dto;

import com.reis.HotelManagementSystem_APi.Security.entities.User;
import com.reis.HotelManagementSystem_APi.Security.entities.enums.UserRole;

public class UserResponseDTO {

	private String id;
	private String login;
	private UserRole userRole;
	
	public UserResponseDTO() {
	}
	
	public UserResponseDTO(User user) {
		this.login = user.getUsername();
		this.id = user.getId();
		this.userRole = user.getUserRole();
	}

	public String getLogin() {
		return login;
	}

	public String getId() {
		return id;
	}

	public UserRole getUserRole() {
		return userRole;
	}
}
