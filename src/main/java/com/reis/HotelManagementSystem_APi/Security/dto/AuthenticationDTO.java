package com.reis.HotelManagementSystem_APi.Security.dto;

import jakarta.validation.constraints.NotNull;

public class AuthenticationDTO {

	@NotNull
	private String login;
	
	@NotNull
	private String password;
	
	public AuthenticationDTO() {
	}

	public AuthenticationDTO(String login, String password) {
		super();
		this.login = login;
		this.password = password;
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
}
