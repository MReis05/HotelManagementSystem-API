package com.reis.HotelManagementSystem_APi.services.exceptions;

public class CheckOutException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CheckOutException(String msg) {
		super(msg);
	}
}
