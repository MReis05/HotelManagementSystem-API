package com.reis.HotelManagementSystem_APi.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.reis.HotelManagementSystem_APi.entities.Stay;

public class StayResponseDTO {

	private Long id;
	private LocalDateTime checkInDate;
	private LocalDateTime checkOutDate;
	
	private Long reservationId;
	
	private GuestSummaryDTO guestSummaryDTO;
	
	private List<IncidentalResponseDTO> incidentalList = new ArrayList<>();
	
	public StayResponseDTO() {
	}
	
	public StayResponseDTO (Stay obj) {
		this.id = obj.getId();
		this.checkInDate = obj.getCheckInDate();
		this.checkOutDate = obj.getCheckOutDate();
		this.reservationId = obj.getReservation().getId();
		this.incidentalList = obj.getIncidental().stream().map(IncidentalResponseDTO::new).collect(Collectors.toList());
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

	public Long getReservationId() {
		return reservationId;
	}

	public List<IncidentalResponseDTO> getIncidentalList() {
		return incidentalList;
	}

	public GuestSummaryDTO getGuestSummaryDTO() {
		return guestSummaryDTO;
	}
}
