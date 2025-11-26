package com.reis.HotelManagementSystem_APi.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.reis.HotelManagementSystem_APi.validation.CheckInStay;
import com.reis.HotelManagementSystem_APi.validation.CheckOutAfterCheckIn;
import com.reis.HotelManagementSystem_APi.validation.DateRangeValidatable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@CheckInStay
@CheckOutAfterCheckIn
public class StayRequestDTO implements DateRangeValidatable {

	@NotNull
	@Positive
	private Long reservationId;
	@NotNull
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime checkInDate;
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime checkOutDate;
	
	public StayRequestDTO() {
	}

	public StayRequestDTO(Long reservationId, LocalDateTime checkInDate, LocalDateTime checkOutDate) {
		this.reservationId = reservationId;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
	}

	public Long getReservationId() {
		return reservationId;
	}

	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}

	public LocalDateTime getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(LocalDateTime checkInDate) {
		this.checkInDate = checkInDate;
	}

	public LocalDateTime getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(LocalDateTime checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	@Override
	@JsonIgnore
	public LocalDateTime checkInDate() {
		return this.checkInDate;
	}

	@Override
	@JsonIgnore
	public LocalDateTime checkOutDate() {
		return this.checkOutDate;
	}
}
