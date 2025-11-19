package com.reis.HotelManagementSystem_APi.validation;

import java.time.LocalDateTime;

public interface DateRangeValidatable {

	LocalDateTime checkInDate();
	
	LocalDateTime checkOutDate();
}
