package com.reis.HotelManagementSystem_APi.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = CheckOutAfterCheckInValidation.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckOutAfterCheckIn {
	
	String message() default "A data de Check-Out precisa ser maior do que a data de Check-In";
	
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
