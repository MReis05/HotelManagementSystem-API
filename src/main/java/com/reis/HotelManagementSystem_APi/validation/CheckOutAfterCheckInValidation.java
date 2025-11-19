package com.reis.HotelManagementSystem_APi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckOutAfterCheckInValidation implements ConstraintValidator<CheckOutAfterCheckIn, DateRangeValidatable> {

	@Override
	public boolean isValid(DateRangeValidatable dto, ConstraintValidatorContext context) {
		if(dto.checkInDate() == null || dto.checkOutDate() == null) {
			return true;
		}
		
		return dto.checkOutDate().isAfter(dto.checkInDate());
	}

}
