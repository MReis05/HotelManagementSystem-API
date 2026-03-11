package com.reis.HotelManagementSystem_APi.Security.services.exceptions;

public class ExistedAccountException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ExistedAccountException() {
		super("Login já em uso");
	}
}
