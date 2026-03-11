package com.reis.HotelManagementSystem_APi.Security.dto;

import com.reis.HotelManagementSystem_APi.Security.entities.enums.UserRole;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class RegisterDTO {

	@NotNull
	private String login;
	
	@NotNull
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
	private String password;
	
	@NotNull
	private UserRole userRole;
	
	public RegisterDTO() {
	}
	
	public RegisterDTO(String login, String password, UserRole userRole) {
		this.login = login;
		this.password = password;
		this.userRole = userRole;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}
}
