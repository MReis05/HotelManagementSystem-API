package com.reis.HotelManagementSystem_APi.services.exceptions;

public class ColumnConstraintException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ColumnConstraintException (String msg) {
		super(msg);
	}
}
