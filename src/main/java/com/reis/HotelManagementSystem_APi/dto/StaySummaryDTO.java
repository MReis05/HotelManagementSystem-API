package com.reis.HotelManagementSystem_APi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.reis.HotelManagementSystem_APi.entities.Stay;

public class StaySummaryDTO {

	private Long id;
	private LocalDateTime checkInDate;
	private LocalDateTime checkOutDate;
	private BigDecimal totalValue;
		
	private GuestSummaryDTO guestSummaryDTO;
	
	public StaySummaryDTO() {
	}
	
	public StaySummaryDTO(Stay obj) {
		this.id = obj.getId();
		this.checkInDate = obj.getCheckInDate();
		this.checkOutDate = obj.getCheckOutDate();
		this.totalValue = obj.getStayTotalValue();
		this.guestSummaryDTO = new GuestSummaryDTO(obj.getReservation().getGuest());
	}

	public Long getId() {
		return id;
	}

	public LocalDateTime getCheckInDate() {
		return checkInDate;
	}

	public LocalDateTime getCheckOutDate() {
		return checkOutDate;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public GuestSummaryDTO getGuestSummaryDTO() {
		return guestSummaryDTO;
	}
}
