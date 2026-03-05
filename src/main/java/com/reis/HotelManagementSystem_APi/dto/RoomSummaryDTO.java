package com.reis.HotelManagementSystem_APi.dto;

import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;

public class RoomSummaryDTO {

	private Long id;
	private Integer roomNumber;
	private RoomStatus status;
	
	public RoomSummaryDTO() {
	}
	
	public RoomSummaryDTO(Room obj) {
		this.id = obj.getId();
		this.roomNumber = obj.getRoomNumber();
		this.status = obj.getStatus();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(Integer roomNumber) {
		this.roomNumber = roomNumber;
	}

	public RoomStatus getStatus() {
		return status;
	}

	public void setStatus(RoomStatus status) {
		this.status = status;
	}
}
