package com.reis.HotelManagementSystem_APi.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.reis.HotelManagementSystem_APi.validation.CheckOutAfterCheckIn;
import com.reis.HotelManagementSystem_APi.validation.DateRangeValidatable;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@CheckOutAfterCheckIn
public class ReservationRequestDTO implements DateRangeValidatable {

	@NotNull
	@Positive
	private Long guestId;
	@NotNull
	@Positive
	private Long roomId;
	@NotNull
	@FutureOrPresent
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate checkInDate;
	@NotNull
	@FutureOrPresent
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate checkOutDate;
	
	public ReservationRequestDTO() {
	}

	public ReservationRequestDTO(Long guestId, Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
		super();
		this.guestId = guestId;
		this.roomId = roomId;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
	}

	public Long getGuestId() {
		return guestId;
	}

	public void setGuestId(Long guestId) {
		this.guestId = guestId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
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

	@Override
	@JsonIgnore
	public LocalDateTime checkInDate() {
		return (this.checkInDate != null) ? this.checkInDate.atStartOfDay() : null;
	}

	@Override
	@JsonIgnore
	public LocalDateTime checkOutDate() {
		return (this.checkOutDate != null) ? this.checkOutDate.atStartOfDay() : null;
	}
}
