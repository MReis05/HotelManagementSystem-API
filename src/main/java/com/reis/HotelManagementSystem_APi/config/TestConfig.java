package com.reis.HotelManagementSystem_APi.config;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

	@Autowired
	private GuestRepository guestRepository;
	
	@Override
	public void run(String... args) throws Exception {
		Guest g1 = new Guest(null, "John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address());
		Guest g2 = new Guest(null, "Maria White", "99999999901","maria@gmail.com", "779118298282", LocalDate.of(2000, 5, 12), new Address());
		Guest g3 = new Guest(null, "Clark Brown", "99999999901","clark@gmail.com", "779118298282", LocalDate.of(1978, 1, 05), new Address());
		
		guestRepository.saveAll(Arrays.asList(g1, g2, g3));
	}

}
