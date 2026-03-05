package com.reis.HotelManagementSystem_APi.Repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;

@DataJpaTest
@ActiveProfiles("test")
public class GuestRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private GuestRepository repository;
	
	@Test
	@DisplayName("Should return true if find a Guest by receiving a CPF")
	void existsByCpfTrueCase() {
		Guest g1 = new Guest("John Green", "14462660013","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		
		Guest persistedGuest = entityManager.persistAndFlush(g1);
		
		boolean existedGuest = repository.existsByCpf(persistedGuest.getCpf());
		
		assertTrue(existedGuest);
	}
	
	@Test
	@DisplayName("Should return false if doens't find a Guest by receiving a CPF")
	void existsByCpfFalseCase() {
		boolean existedGuest = repository.existsByCpf("000.001.002-14");
		
		assertFalse(existedGuest);
	}
}
