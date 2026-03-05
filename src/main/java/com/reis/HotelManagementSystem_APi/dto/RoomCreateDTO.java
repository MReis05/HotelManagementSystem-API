package com.reis.HotelManagementSystem_APi.dto;

import java.math.BigDecimal;

import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RoomCreateDTO {

	@NotNull
	@Positive
	private Integer roomNumber;
	@NotNull
	@Positive
	private BigDecimal pricePerNight;
	@NotBlank
	private String description;
	@NotNull
	private RoomStatus status;
	@NotNull
	private RoomType type;
	
	public RoomCreateDTO() {
	}

	public RoomCreateDTO(Integer roomNumber, BigDecimal pricePerNight, String description, RoomStatus status, RoomType type) {
		super();
		this.roomNumber = roomNumber;
		this.pricePerNight = pricePerNight;
		this.description = description;
		this.status = status;
		this.type = type;
	}

	public Integer getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(Integer roomNumber) {
		this.roomNumber = roomNumber;
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

	public RoomType getType() {
		return type;
	}

	public void setType(RoomType type) {
		this.type = type;
	}
}
