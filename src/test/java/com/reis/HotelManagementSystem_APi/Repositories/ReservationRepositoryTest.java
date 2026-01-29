package com.reis.HotelManagementSystem_APi.Repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.ReservationRepository;

@DataJpaTest
@ActiveProfiles("test")
public class ReservationRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private ReservationRepository repository;
	
	@Test
	@DisplayName("Should return a list of Reservation by finding existing status")
	void findByStatusSuccessCase() {
		Reservation reservation = createStandardReservation();
		
		entityManager.persistAndFlush(reservation.getGuest());
		entityManager.persistAndFlush(reservation.getRoom());
		
		Reservation persitedReservation = entityManager.persistAndFlush(reservation);
		
		List<Reservation> listReceived = repository.findByStatus(persitedReservation.getStatus());
		
		assertNotNull(listReceived);
		assertEquals(ReservationStatus.PENDENTE, listReceived.get(0).getStatus());
	}
	
	@Test
	@DisplayName("Shoud return an emtpy list when Reservation status is not found")
	void findByStatusNotFoundCase() {
		List<Reservation> listReceived = repository.findByStatus(ReservationStatus.CANCELADA);
		
		assertNotNull(listReceived);
		assertTrue(listReceived.isEmpty());
	}
	
	private Reservation createStandardReservation() {
		Reservation rv = new Reservation(LocalDate.of(2026, 02, 01), LocalDate.of(2026, 02, 04), ReservationStatus.PENDENTE);
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		rv.setTotalValue(new BigDecimal("570.00"));
		rv.setRoom(r1);
		rv.setGuest(g1);
		return rv;
	}
}
