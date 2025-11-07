package com.reis.HotelManagementSystem_APi.dto;

import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;

public class RoomResponseDTO {

	
	private Long id;
	private Integer number;
	private Double pricePerNight;
	private String description;
	private RoomStatus status;
	private RoomType type;
	
	public RoomResponseDTO() {
	}
	
	public RoomResponseDTO(Room room) {
		this.id = room.getId();
		this.number = room.getNumber();
		this.pricePerNight = room.getPricePerNight();
		this.description = room.getDescription();
		this.status = room.getStatus();
		this.type = room.getType();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
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

	public RoomType getType() {
		return type;
	}

	public void setType(RoomType type) {
		this.type = type;
	}
}
