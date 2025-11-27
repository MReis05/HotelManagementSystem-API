package com.reis.HotelManagementSystem_APi.dto;

import com.reis.HotelManagementSystem_APi.entities.enums.PaymentType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentRequestDTO {

	@NotNull
	private PaymentType type;
	@NotNull
	@Positive
	private Double amount;
	
	@NotNull
	@Positive
	private Long reservationId;
	
	public PaymentRequestDTO() {
	}
	
	public PaymentRequestDTO(PaymentType type, Double amount, Long reservationId) {
		this.type = type;
		this.amount = amount;
		this.reservationId = reservationId;
	}

	public PaymentType getType() {
		return type;
	}

	public void setType(PaymentType type) {
		this.type = type;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getReservationId() {
		return reservationId;
	}

	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}
}
