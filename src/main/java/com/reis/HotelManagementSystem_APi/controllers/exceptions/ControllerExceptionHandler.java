package com.reis.HotelManagementSystem_APi.controllers.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.reis.HotelManagementSystem_APi.services.exceptions.CheckInDateException;
import com.reis.HotelManagementSystem_APi.services.exceptions.CheckOutException;
import com.reis.HotelManagementSystem_APi.services.exceptions.DatabaseException;
import com.reis.HotelManagementSystem_APi.services.exceptions.InvalidActionException;
import com.reis.HotelManagementSystem_APi.services.exceptions.InvalidDurationReservationException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;
import com.reis.HotelManagementSystem_APi.services.exceptions.RoomUnavailableException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
		String error = "Resource not found";
		HttpStatus status = HttpStatus.NOT_FOUND;
		StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(DatabaseException.class)
	public ResponseEntity<StandardError> databaseError(DatabaseException e, HttpServletRequest request){
		String error = "Database Error";
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}

	@ExceptionHandler(RoomUnavailableException.class)
	public ResponseEntity<StandardError> roomUnavailable (RoomUnavailableException e, HttpServletRequest request){
		String error = "Room Unavailable";
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(InvalidDurationReservationException.class)
	public ResponseEntity<StandardError> invalidDuration(InvalidDurationReservationException e, HttpServletRequest request){
		String error = "Invalid Duration";
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(InvalidActionException.class)
	public ResponseEntity<StandardError> invalidAction(InvalidActionException e, HttpServletRequest request){
		String error = "Invalid Action";
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(CheckInDateException.class)
	public ResponseEntity<StandardError> invalidCheckInDateStay(CheckInDateException e, HttpServletRequest request){
		String error = "Invalid Check-In Date";
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(CheckOutException.class)
	public ResponseEntity<StandardError> checkOut(CheckOutException e, HttpServletRequest request){
		String error = "Error making Check-Out";
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardError> validationsException (MethodArgumentNotValidException e, HttpServletRequest request){
		String error = "Validation Error";
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		ValidationError err = new ValidationError(Instant.now(), status.value(), error, "Erro na validação dos campos", request.getRequestURI());
		
		for(FieldError f : e.getBindingResult().getFieldErrors() ) {
			err.addError(f.getField(), f.getDefaultMessage());
		}
		
		return ResponseEntity.status(status).body(err);
	}
}
