package com.reis.HotelManagementSystem_APi.services.exceptions;

public class InvalidDurationReservationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidDurationReservationException () {
		super("Diária inferior a um dia");
	}
}
