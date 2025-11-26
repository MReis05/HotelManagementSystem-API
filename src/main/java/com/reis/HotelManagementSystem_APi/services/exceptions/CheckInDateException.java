package com.reis.HotelManagementSystem_APi.services.exceptions;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CheckInDateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CheckInDateException(LocalDateTime checkInStay, LocalDate checkInReservation) {
		super("A data de Check-In informada é diferente da data informada na reserva. Data de Check-In na reserva: " + checkInReservation
				+ ". Data de Check-In na estadia: " + checkInStay);
	}
}
