package com.reis.HotelManagementSystem_APi.IntegrationTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.repositories.ReservationRepository;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReservationIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private ReservationRepository repository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private GuestRepository guestRepository;
	
	private Long reservationId;
	
	private Long roomId;

	private Long guestId;

	
	@BeforeEach
	void injectObjects() {
		Guest g1 = new Guest("John Green", "14462660013","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		
		g1 = this.guestRepository.save(g1);
		r1 = this.roomRepository.save(r1);
		
		Reservation rv = new Reservation(LocalDate.now(), LocalDate.now().plusDays(4L), ReservationStatus.PENDENTE);
		rv.setTotalValue(new BigDecimal("570.00"));
		rv.setRoom(r1);
		rv.setGuest(g1);
		
		rv = this.repository.save(rv);
		
		this.guestId = g1.getId();
		this.roomId = r1.getId();
		this.reservationId = rv.getId();
	}
	
	@Test
	@DisplayName("Should return all Reservations in Database and 200 OK status (End-to-End)")
	void findAllSuccessCase() throws Exception {
		mockMvc.perform(
				get("/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].id").value(reservationId))
		.andExpect(jsonPath("$[0].checkInDate").value(LocalDate.now().toString()))
		.andExpect(jsonPath("$[0].checkOutDate").value(LocalDate.now().plusDays(4L).toString()))
		.andExpect(jsonPath("$[0].status").value(ReservationStatus.PENDENTE.name()))
		.andExpect(jsonPath("$[0].totalValue").value(570.00));
	}
	
	@Test
	@DisplayName("Should return a List of Reservations with right status and 200 OK status")
	void findByStatusSuccessCase() throws Exception {
		mockMvc.perform(
				get("/reservations?status=" + ReservationStatus.PENDENTE.name())
				.contentType(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].id").value(reservationId))
		.andExpect(jsonPath("$[0].checkInDate").value(LocalDate.now().toString()))
		.andExpect(jsonPath("$[0].checkOutDate").value(LocalDate.now().plusDays(4L).toString()))
		.andExpect(jsonPath("$[0].status").value(ReservationStatus.PENDENTE.name()))
		.andExpect(jsonPath("$[0].roomSummaryDTO.id").value(roomId))
		.andExpect(jsonPath("$[0].guestSummaryDTO.id").value(guestId))
		.andExpect(jsonPath("$[0].totalValue").value(570.00));
	}
	
	@Test
	@DisplayName("Should return empty list when filtering by non-existent status")
	void findByStatusNotFoundCase() throws Exception {
		mockMvc.perform(
				get("/reservations?status=" + ReservationStatus.CONFIRMADA.name())
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isEmpty());
	}
	
	@Test
	@DisplayName("Should return Reservation when finding by existing Id and 200 Ok status (End-to-End)")
	void findByIdSuccessCase() throws Exception {
		mockMvc.perform(
				get("/reservations/{id}", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(reservationId))
				.andExpect(jsonPath("$.checkInDate").value(LocalDate.now().toString()))
				.andExpect(jsonPath("$.checkOutDate").value(LocalDate.now().plusDays(4L).toString()))
				.andExpect(jsonPath("$.status").value(ReservationStatus.PENDENTE.name()))
				.andExpect(jsonPath("$.roomSummaryDTO.id").value(roomId))
				.andExpect(jsonPath("$.guestSummaryDTO.id").value(guestId))
				.andExpect(jsonPath("$.totalValue").value(570.00));
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn't find Room")
	void findByIdResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				get("/reservations/{id}", (reservationId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (reservationId + 98)));
	}
}
