package com.reis.HotelManagementSystem_APi.services.exceptions;

public class InvalidActionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidActionException() {
		super("Ação Invalida");
	}
}
