package com.reis.HotelManagementSystem_APi.services.exceptions;

public class RoomUnavailableException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RoomUnavailableException (Integer roomNumber) {
		super("Quarto não disponivel para reserva. Número do quarto: " + roomNumber);
	}
}
