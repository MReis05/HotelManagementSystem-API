package com.reis.HotelManagementSystem_APi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;

public class ReservationResponseDTO {

	private Long id;
	private LocalDate checkInDate;
	private LocalDate checkOutDate;
	private BigDecimal totalValue;
	private ReservationStatus status;
	
	private RoomSummaryDTO room;
	private GuestSummaryDTO guest;
	
	private List<PaymentSummaryDTO> paymentsSummary = new ArrayList<>();
	
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
		this.paymentsSummary = obj.getPayments().stream().map(PaymentSummaryDTO::new).collect(Collectors.toList());
	}

	public Long getId() {
		return id;
	}

	public LocalDate getCheckInDate() {
		return checkInDate;
	}

	public LocalDate getCheckOutDate() {
		return checkOutDate;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public RoomSummaryDTO getRoomSummaryDTO() {
		return room;
	}

	public GuestSummaryDTO getGuestSummaryDTO() {
		return guest;
	}

	public List<PaymentSummaryDTO> getPaymentsSummary() {
		return paymentsSummary;
	}
}
