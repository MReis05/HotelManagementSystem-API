package com.reis.HotelManagementSystem_APi.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = CheckInStayValidation.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckInStay {

	String message() default "A data de Check-In precisa ser igual ao dia atual";
	
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
