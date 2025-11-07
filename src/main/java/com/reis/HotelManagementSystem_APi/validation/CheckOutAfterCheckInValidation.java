package com.reis.HotelManagementSystem_APi.validation;

import com.reis.HotelManagementSystem_APi.dto.ReservationRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckOutAfterCheckInValidation implements ConstraintValidator<CheckOutAfterCheckIn, ReservationRequestDTO> {

	@Override
	public boolean isValid(ReservationRequestDTO dto, ConstraintValidatorContext context) {
		if(dto.getCheckInDate() == null || dto.getCheckOutDate() == null) {
			return true;
		}
		
		return dto.getCheckOutDate().isAfter(dto.getCheckInDate());
	}

}
