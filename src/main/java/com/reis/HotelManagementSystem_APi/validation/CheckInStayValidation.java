package com.reis.HotelManagementSystem_APi.validation;

import java.time.LocalDate;

import com.reis.HotelManagementSystem_APi.dto.StayRequestDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckInStayValidation implements ConstraintValidator<CheckInStay, StayRequestDTO> {

	@Override
	public boolean isValid(StayRequestDTO dto, ConstraintValidatorContext context) {
		if(dto.getCheckInDate() == null) {
			return true;
		}
		
		return dto.getCheckInDate().toLocalDate().isEqual(LocalDate.now());
	}

}
