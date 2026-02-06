package com.reis.HotelManagementSystem_APi.IntegrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

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
import com.reis.HotelManagementSystem_APi.dto.StayRequestDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.Stay;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.repositories.ReservationRepository;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;
import com.reis.HotelManagementSystem_APi.repositories.StayRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class StayIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private StayRepository repository;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private GuestRepository guestRepository;
	
	private Long stayId;
	
	private Long reservationOngoingId;
	
	private Long reservationConfirmedId;
	
	private LocalDateTime checkInDate;
	
	@BeforeEach
	void injectObjects() {
		Guest g1 = new Guest("John Green", "14462660013", "john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05),
				new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		
		Guest g2 = new Guest("Maria Green", "011.180.880-42", "maria@gmail.com", "859901902120", LocalDate.of(2000, 1, 05),
				new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL,
				RoomType.SOLTEIRO);
		
		Room r2 = new Room(2, new BigDecimal("230.00"), "Quarto com Ar-Condicionado", RoomStatus.DISPONIVEL,
				RoomType.CASAL);
		
		Reservation rv = new Reservation(LocalDate.now(), LocalDate.now().plusDays(4L), ReservationStatus.EM_CURSO);
		rv.setTotalValue(new BigDecimal("570.00"));
		rv.setRoom(r1);
		rv.setGuest(g1);
		
		Reservation rv2 = new Reservation(LocalDate.now(), LocalDate.now().plusDays(4L), ReservationStatus.CONFIRMADA);
		rv2.setTotalValue(new BigDecimal("690.00"));
		rv2.setRoom(r2);
		rv2.setGuest(g2);
		
		r1.getReservations().add(rv);
		r2.getReservations().add(rv2);
		
		this.guestRepository.saveAll(Arrays.asList(g1, g2));
		this.roomRepository.saveAll(Arrays.asList(r1, r2));
		
		rv = this.reservationRepository.save(rv);
		rv2 = this.reservationRepository.save(rv2);
		
		Stay stay = new Stay(LocalDateTime.now().withNano(0), null, rv);
		
		stay = this.repository.save(stay);
		
		reservationOngoingId = rv.getId();
		reservationConfirmedId = rv2.getId();
		stayId = stay.getId();
		checkInDate = stay.getCheckInDate();
	}
	
	@Test
	@DisplayName("Should return all Stays and 200 OK status (End-to-End")
	void findAllSuccessCase() throws Exception {
		mockMvc.perform(
				get("/stays")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(stayId))
				.andExpect(jsonPath("$[0].checkInDate").value(checkInDate.toString()))
				.andExpect(jsonPath("$[0].totalValue").value(570.00))
				.andExpect(jsonPath("$[0].guestSummaryDTO.name").value("John Green"));
	}
	
	@Test
	@DisplayName("Should return Stay when finding by existing Id and 200 Ok status (End-to-End)")
	void findByIdSuccessCase() throws Exception {
		mockMvc.perform(
				get("/stays/{id}", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(stayId))
				.andExpect(jsonPath("$.checkInDate").value(checkInDate.toString()))
				.andExpect(jsonPath("$.totalValue").value(570.00))
				.andExpect(jsonPath("$.reservationId").value(reservationOngoingId))
				.andExpect(jsonPath("$.guestSummaryDTO.name").value("John Green"))
				.andExpect(jsonPath("$.incidentalsList").isArray());	
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found when doesn't find Stay")
	void findByIdResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				get("/stays/{id}", (stayId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (stayId + 98)));
	}
	
	@Test
	@DisplayName("Should make Check-In and return 201 Created (End-to-End)")
	void checkInSuccessCase() throws Exception {
		StayRequestDTO inputDTO = new StayRequestDTO(reservationConfirmedId, LocalDateTime.now().withNano(0), null);
		LocalDateTime inputDate = inputDTO.getCheckInDate();
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.checkInDate").value(inputDate.toString()))
				.andExpect(jsonPath("$.totalValue").value(690.00))
				.andExpect(jsonPath("$.reservationId").value(reservationConfirmedId))
				.andExpect(jsonPath("$.guestSummaryDTO.name").value("Maria Green"))
				.andExpect(jsonPath("$.incidentalsList").isArray())
				.andExpect(header().exists("Location"));
		
		Stay staySaved = repository.findAll().stream().filter(s -> s.getReservation().getId().equals(reservationConfirmedId))
					.findFirst().orElseThrow(() -> new AssertionError("Reservation não encontrada"));
		
		assertEquals(2, repository.count());
		assertEquals(inputDate, staySaved.getCheckInDate());
		assertNull(staySaved.getCheckOutDate());
		assertEquals(reservationConfirmedId, staySaved.getReservation().getId());
		assertEquals("Maria Green", staySaved.getReservation().getGuest().getName());
		assertTrue(staySaved.getIncidentals().isEmpty());
		assertEquals(new BigDecimal("690.00"), staySaved.getStayTotalValue());
	}
}
