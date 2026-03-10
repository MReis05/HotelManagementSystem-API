package com.reis.HotelManagementSystem_APi.Security.entities.enums;

public enum UserRole {

	ADMIN("admin"),
	USER("user");
	
	private String role;
	
	 UserRole(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}
}
