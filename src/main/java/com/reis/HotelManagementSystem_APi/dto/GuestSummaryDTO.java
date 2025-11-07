package com.reis.HotelManagementSystem_APi.dto;

import com.reis.HotelManagementSystem_APi.entities.Guest;

public class GuestSummaryDTO {

	private Long id;
	private String name;
	private String email;
	
	public GuestSummaryDTO (){
	}
	
	public GuestSummaryDTO(Guest obj) {
		this.id = obj.getId();
		this.name = obj.getName();
		this.email = obj.getEmail();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
