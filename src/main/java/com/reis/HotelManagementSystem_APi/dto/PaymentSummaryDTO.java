package com.reis.HotelManagementSystem_APi.dto;

import java.time.Instant;

import com.reis.HotelManagementSystem_APi.entities.Payment;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentType;

public class PaymentSummaryDTO {

	private Double amount;
	private Instant moment;
	
	private PaymentStatus status;
	private PaymentType type;
	
	public PaymentSummaryDTO () {
	}
	
	public PaymentSummaryDTO(Payment obj) {
		this.amount = obj.getAmount();
		this.moment = obj.getMoment();
		this.status = obj.getStatus();
		this.type = obj.getType();
	}

	public Double getAmount() {
		return amount;
	}

	public Instant getMoment() {
		return moment;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public PaymentType getType() {
		return type;
	}
}

