package com.reis.HotelManagementSystem_APi.dto;

import java.math.BigDecimal;

import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;

import jakarta.validation.constraints.Positive;

public class RoomUpdateDTO {
	
	@Positive
	private BigDecimal pricePerNight;
	private String description;
	private RoomStatus status;
	
	public RoomUpdateDTO () {
	}

	public RoomUpdateDTO(BigDecimal pricePerNight, String description, RoomStatus status) {
		super();
		this.pricePerNight = pricePerNight;
		this.description = description;
		this.status = status;
	}

	public BigDecimal getPricePerNight() {
		return pricePerNight;
	}

	public void setPricePerNight(BigDecimal pricePerNight) {
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
