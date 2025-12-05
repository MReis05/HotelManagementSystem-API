package com.reis.HotelManagementSystem_APi.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.reis.HotelManagementSystem_APi.entities.Payment;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentType;

public class PaymentResponseDTO {

	private Long id;
	private Instant moment;
	private BigDecimal amount;
	
	private PaymentStatus status;
	private PaymentType type;
	
	private Long reservationId;
	
	public PaymentResponseDTO() {
	}
	
	public PaymentResponseDTO(Payment obj) {
		this.id = obj.getId();
		this.moment = obj.getMoment();
		this.status = obj.getStatus();
		this.type = obj.getType();
		this.amount = obj.getAmount();
		this.reservationId = obj.getReservation().getId();
	}

	public Long getId() {
		return id;
	}

	public Instant getMoment() {
		return moment;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public PaymentType getType() {
		return type;
	}

	public Long getReservationId() {
		return reservationId;
	}
}
