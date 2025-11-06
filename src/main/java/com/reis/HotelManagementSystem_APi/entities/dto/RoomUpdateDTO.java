package com.reis.HotelManagementSystem_APi.entities.dto;

import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;

import jakarta.validation.constraints.Positive;

public class RoomUpdateDTO {
	
	@Positive
	private Double pricePerNight;
	private String description;
	private RoomStatus status;
	
	public RoomUpdateDTO () {
	}

	public RoomUpdateDTO(Double pricePerNight, String description, RoomStatus status) {
		super();
		this.pricePerNight = pricePerNight;
		this.description = description;
		this.status = status;
	}

	public Double getPricePerNight() {
		return pricePerNight;
	}

	public void setPricePerNight(Double pricePerNight) {
		this.pricePerNight = pricePerNight;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RoomStatus getStatus() {
		return status;
	}

	public void setStatus(RoomStatus status) {
		this.status = status;
	}
}
