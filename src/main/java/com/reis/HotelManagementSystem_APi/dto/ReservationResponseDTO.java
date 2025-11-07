package com.reis.HotelManagementSystem_APi.dto;

import java.time.LocalDate;

import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;

public class ReservationResponseDTO {

	private Long id;
	private LocalDate checkInDate;
	private LocalDate checkOutDate;
	private Double totalValue;
	private ReservationStatus status;
	
	private RoomSummaryDTO room;
	private GuestSummaryDTO guest;
	
	public ReservationResponseDTO() {
	}
	
	public ReservationResponseDTO(Reservation obj) {
		this.id = obj.getId();
		this.checkInDate = obj.getCheckInDate();
		this.checkOutDate = obj.getCheckOutDate();
		this.totalValue = obj.getTotalValue();
		this.status = obj.getStatus();
		this.room = new RoomSummaryDTO(obj.getRoom());
		this.guest = new GuestSummaryDTO(obj.getGuest());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(LocalDate checkInDate) {
		this.checkInDate = checkInDate;
	}

	public LocalDate getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(LocalDate checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public Double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(Double totalValue) {
		this.totalValue = totalValue;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public void setStatus(ReservationStatus status) {
		this.status = status;
	}

	public RoomSummaryDTO getRoomSummaryDTO() {
		return room;
	}

	public void setRoomSummaryDTO(RoomSummaryDTO room) {
		this.room = room;
	}

	public GuestSummaryDTO getGuestSummaryDTO() {
		return guest;
	}

	public void setGuestSummaryDTO(GuestSummaryDTO guest) {
		this.guest = guest;
	}
}
