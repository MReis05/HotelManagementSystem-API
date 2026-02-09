package com.reis.HotelManagementSystem_APi.IntegrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import com.reis.HotelManagementSystem_APi.dto.PaymentRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationRequestDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentType;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.repositories.ReservationRepository;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;

import jakarta.persistence.EntityManager;

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
	
	@Autowired
	private EntityManager entityManager;

	private Long reservationId;
	
	private Long badReservationId;

	private Long roomId;

	private Long badRoomId;

	private Long guestId;

	@BeforeEach
	void injectObjects() {
		Guest g1 = new Guest("John Green", "14462660013", "john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05),
				new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL,
				RoomType.SOLTEIRO);
		Room r2 = new Room(45, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.MANUTENCAO,
				RoomType.SOLTEIRO);

		g1 = this.guestRepository.save(g1);
		r1 = this.roomRepository.save(r1);
		r2 = this.roomRepository.save(r2);

		Reservation rv = new Reservation(LocalDate.now(), LocalDate.now().plusDays(4L), ReservationStatus.PENDENTE);
		rv.setTotalValue(new BigDecimal("570.00"));
		rv.setRoom(r1);
		rv.setGuest(g1);
		
		Reservation rv2 = new Reservation(LocalDate.now().minusDays(1), LocalDate.now().plusDays(3L), ReservationStatus.CANCELADA);
		rv2.setTotalValue(new BigDecimal("570.00"));
		rv2.setRoom(r1);
		rv2.setGuest(g1);

		rv = this.repository.save(rv);
		rv2 = this.repository.save(rv2);

		this.guestId = g1.getId();
		r1.getReservations().add(rv2);
		r1.getReservations().add(rv);
		roomRepository.save(r1);
		this.roomId = r1.getId();
		this.reservationId = rv.getId();
		this.badRoomId = r2.getId();
		this.badReservationId = rv2.getId();
	}

	@Test
	@DisplayName("Should return all Reservations in Database and 200 OK status (End-to-End)")
	void findAllSuccessCase() throws Exception {
		mockMvc.perform(get("/reservations")
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
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn't find reservation")
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

	@Test
	@DisplayName("Should create a Reservation in Database and return 201 Created status (End-to-End)")
	void insertSuccessCase() throws Exception {
		ReservationRequestDTO inputDTO = new ReservationRequestDTO(guestId, roomId, LocalDate.now().plusDays(5L),
				LocalDate.now().plusDays(9));

		String jsonBody = mapper.writeValueAsString(inputDTO);

		mockMvc.perform(
				post("/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.checkInDate").value(LocalDate.now().plusDays(5L).toString()))
				.andExpect(jsonPath("$.checkOutDate").value(LocalDate.now().plusDays(9L).toString()))
				.andExpect(jsonPath("$.status").value(ReservationStatus.PENDENTE.name()))
				.andExpect(jsonPath("$.totalValue").value(760.00))
				.andExpect(jsonPath("$.roomSummaryDTO.id").value(roomId))
				.andExpect(jsonPath("$.guestSummaryDTO.id").value(guestId)).andExpect(header().exists("Location"));

		Reservation savedReservation = repository.findAll().stream()
				.filter(rv -> rv.getCheckInDate().equals(LocalDate.now().plusDays(5L))).findFirst()
				.orElseThrow(() -> new AssertionError("Reservation não encontrada"));

		assertEquals(3, repository.count());
		assertEquals(LocalDate.now().plusDays(5L), savedReservation.getCheckInDate());
		assertEquals(LocalDate.now().plusDays(9L), savedReservation.getCheckOutDate());
		assertEquals(new BigDecimal("760.00"), savedReservation.getTotalValue());
		assertEquals(ReservationStatus.PENDENTE, savedReservation.getStatus());
		assertEquals(roomId, savedReservation.getRoom().getId());
		assertEquals(guestId, savedReservation.getGuest().getId());
	}

	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn't find Room or Guest")
	void insertResourceNotFoundCase() throws Exception {
		ReservationRequestDTO inputDTO = new ReservationRequestDTO((guestId + 98), roomId, LocalDate.now().plusDays(5L),
				LocalDate.now().plusDays(9));

		String jsonBody = mapper.writeValueAsString(inputDTO);

		mockMvc.perform(
				post("/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (guestId + 98)));

		assertEquals(2, repository.count());
	}

	@Test
	@DisplayName("Should throw a RoomUnavailableException when Room is under maintenance and 400 Bad Request status")
	void insertRoomUnavailableCase() throws Exception {
		ReservationRequestDTO inputDTO = new ReservationRequestDTO(guestId, badRoomId, LocalDate.now().plusDays(5L),
				LocalDate.now().plusDays(9));

		String jsonBody = mapper.writeValueAsString(inputDTO);

		mockMvc.perform(
				post("/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Room Unavailable"))
				.andExpect(jsonPath("$.message").value("Quarto não disponivel para reserva. Número do quarto: " + 45));

		assertEquals(2, repository.count());
	}

	@Test
	@DisplayName("Should throw a RoomUnavailableException when Room already has a reservation in that Date")
	void insertConflitDatesCase() throws Exception {
		ReservationRequestDTO inputDTO = new ReservationRequestDTO(guestId, roomId, LocalDate.now().plusDays(1L),
				LocalDate.now().plusDays(5L));

		String jsonBody = mapper.writeValueAsString(inputDTO);

		mockMvc.perform(
				post("/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Room Unavailable"))
				.andExpect(jsonPath("$.message").value("Quarto não disponivel para reserva. Número do quarto: " + 1));

		assertEquals(2, repository.count());
	}

	@Test
	@DisplayName("Should update Reservation and return 200 OK status (End-to-End")
	void updateSuccessCase() throws Exception {
		ReservationRequestDTO inputDTO = new ReservationRequestDTO();
		inputDTO.setCheckInDate(LocalDate.now().plusDays(2L));
		inputDTO.setCheckOutDate(LocalDate.now().plusDays(8L));

		String jsonBody = mapper.writeValueAsString(inputDTO);

		mockMvc.perform(
				put("/reservations/{id}/update", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.checkInDate").value(LocalDate.now().plusDays(2L).toString()))
				.andExpect(jsonPath("$.checkOutDate").value(LocalDate.now().plusDays(8L).toString()))
				.andExpect(jsonPath("$.status").value(ReservationStatus.PENDENTE.name()))
				.andExpect(jsonPath("$.totalValue").value(1140.00))
				.andExpect(jsonPath("$.roomSummaryDTO.id").value(roomId))
				.andExpect(jsonPath("$.guestSummaryDTO.id").value(guestId));

		Reservation savedReservation = repository.findById(reservationId).orElseThrow();

		assertEquals(2, repository.count());
		assertEquals(LocalDate.now().plusDays(2L), savedReservation.getCheckInDate());
		assertEquals(LocalDate.now().plusDays(8L), savedReservation.getCheckOutDate());
		assertEquals(new BigDecimal("1140.00"), savedReservation.getTotalValue());
		assertEquals(ReservationStatus.PENDENTE, savedReservation.getStatus());
		assertEquals(roomId, savedReservation.getRoom().getId());
		assertEquals(guestId, savedReservation.getGuest().getId());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 NotFound status when doesn't find Reservation to update")
	void updateResourceNotFoundCase() throws Exception {
		ReservationRequestDTO inputDTO = new ReservationRequestDTO();
		inputDTO.setCheckInDate(LocalDate.now().plusDays(2L));
		inputDTO.setCheckOutDate(LocalDate.now().plusDays(8L));

		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/reservations/{id}/update", (reservationId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (reservationId + 98)));
		
		Reservation savedReservation = repository.findById(reservationId).orElseThrow();

		assertEquals(2, repository.count());
		assertEquals(LocalDate.now(), savedReservation.getCheckInDate());
		assertEquals(LocalDate.now().plusDays(4L), savedReservation.getCheckOutDate());
		assertEquals(new BigDecimal("570.00"), savedReservation.getTotalValue());
		assertEquals(ReservationStatus.PENDENTE, savedReservation.getStatus());
		assertEquals(roomId, savedReservation.getRoom().getId());
		assertEquals(guestId, savedReservation.getGuest().getId());
	}
	
	@Test
	@DisplayName("Should throw an InvalidActionException when Reservation is already completed or canceled")
	void updateInvalidReservationActionCase() throws Exception {
		ReservationRequestDTO inputDTO = new ReservationRequestDTO();
		inputDTO.setCheckInDate(LocalDate.now().plusDays(2L));
		inputDTO.setCheckOutDate(LocalDate.now().plusDays(8L));

		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/reservations/{id}/update", badReservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Invalid Action"))
				.andExpect(jsonPath("$.message").value("Não é possivel atualizar dados de uma reserva cancelada ou concluída"));
		
		Reservation savedReservation = repository.findById(reservationId).orElseThrow();

		assertEquals(2, repository.count());
		assertEquals(LocalDate.now(), savedReservation.getCheckInDate());
		assertEquals(LocalDate.now().plusDays(4L), savedReservation.getCheckOutDate());
		assertEquals(new BigDecimal("570.00"), savedReservation.getTotalValue());
		assertEquals(ReservationStatus.PENDENTE, savedReservation.getStatus());
		assertEquals(roomId, savedReservation.getRoom().getId());
		assertEquals(guestId, savedReservation.getGuest().getId());
	}
	
	@Test
	@DisplayName("Should throw a RoomUnavailableException when Room is under maintenance and 400 Bad Request status")
	void updateRoomUnavailableCase() throws Exception {
		ReservationRequestDTO inputDTO = new ReservationRequestDTO(guestId, badRoomId, LocalDate.now().plusDays(5L),
				LocalDate.now().plusDays(9));

		String jsonBody = mapper.writeValueAsString(inputDTO);

		mockMvc.perform(
				put("/reservations/{id}/update", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Room Unavailable"))
				.andExpect(jsonPath("$.message").value("Quarto não disponivel para reserva. Número do quarto: " + 45));
		
		entityManager.clear();
		
		Reservation savedReservation = repository.findById(reservationId).orElseThrow();

		assertEquals(2, repository.count());
		assertEquals(LocalDate.now(), savedReservation.getCheckInDate());
		assertEquals(LocalDate.now().plusDays(4L), savedReservation.getCheckOutDate());
		assertEquals(new BigDecimal("570.00"), savedReservation.getTotalValue());
		assertEquals(ReservationStatus.PENDENTE, savedReservation.getStatus());
		assertEquals(roomId, savedReservation.getRoom().getId());
		assertEquals(guestId, savedReservation.getGuest().getId());
	}
	
	@Test
	@DisplayName("Should throw a RoomUnavailableException when Room already has a reservation in that Date")
	void updateConflitDatesCase() throws Exception {
		Reservation rv = repository.findById(badReservationId).orElseThrow();
		rv.setStatus(ReservationStatus.PENDENTE);
		repository.save(rv);
		
		ReservationRequestDTO inputDTO = new ReservationRequestDTO(guestId, roomId, LocalDate.now(),
				LocalDate.now().plusDays(4));

		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/reservations/{id}/update", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Room Unavailable"))
				.andExpect(jsonPath("$.message").value("Quarto não disponivel para reserva. Número do quarto: " + 1));
		
		Reservation savedReservation = repository.findById(reservationId).orElseThrow();

		assertEquals(2, repository.count());
		assertEquals(LocalDate.now(), savedReservation.getCheckInDate());
		assertEquals(LocalDate.now().plusDays(4L), savedReservation.getCheckOutDate());
		assertEquals(new BigDecimal("570.00"), savedReservation.getTotalValue());
		assertEquals(ReservationStatus.PENDENTE, savedReservation.getStatus());
		assertEquals(roomId, savedReservation.getRoom().getId());
		assertEquals(guestId, savedReservation.getGuest().getId());
	}
	
	@Test
	@DisplayName("Should cancel Reservation and return 200 OK status (End-to-End")
	void cancelReservationSuccessCase() throws Exception {
		mockMvc.perform(
				patch("/reservations/{id}/cancel", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value(ReservationStatus.CANCELADA.name()));
		
		Reservation savedReservation = repository.findById(reservationId).orElseThrow();
		
		assertEquals(ReservationStatus.CANCELADA, savedReservation.getStatus());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when doesn't find Reservation to cancel")
	void cancelReservationNotFoundCase() throws Exception {
		mockMvc.perform(
				patch("/reservations/{id}/cancel", (reservationId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (reservationId + 98)));

		Reservation savedReservation = repository.findById(reservationId).orElseThrow();
		
		assertEquals(ReservationStatus.PENDENTE, savedReservation.getStatus());
	}
	
	@Test
	@DisplayName("Should confirm Reservation and return 200 OK status (End-to-End")
	void confirmReservationSuccessCase() throws Exception {
		PaymentRequestDTO inputDTO = new PaymentRequestDTO(PaymentType.PIX, new BigDecimal("300.00"), reservationId);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/reservations/{id}/payment", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(reservationId))
				.andExpect(jsonPath("$.checkInDate").value(LocalDate.now().toString()))
				.andExpect(jsonPath("$.checkOutDate").value(LocalDate.now().plusDays(4L).toString()))
				.andExpect(jsonPath("$.status").value(ReservationStatus.CONFIRMADA.name()))
				.andExpect(jsonPath("$.roomSummaryDTO.id").value(roomId))
				.andExpect(jsonPath("$.guestSummaryDTO.id").value(guestId))
				.andExpect(jsonPath("$.totalValue").value(570.00))
				.andExpect(jsonPath("$.paymentsSummary[0].status").value(PaymentStatus.APROVADO.name()));
		
		Reservation savedReservation = repository.findById(reservationId).orElseThrow();

		assertEquals(2, repository.count());
		assertEquals(LocalDate.now(), savedReservation.getCheckInDate());
		assertEquals(LocalDate.now().plusDays(4L), savedReservation.getCheckOutDate());
		assertEquals(new BigDecimal("570.00"), savedReservation.getTotalValue());
		assertEquals(ReservationStatus.CONFIRMADA, savedReservation.getStatus());
		assertEquals(roomId, savedReservation.getRoom().getId());
		assertEquals(guestId, savedReservation.getGuest().getId());
		assertEquals(PaymentStatus.APROVADO, savedReservation.getPayments().get(0).getStatus());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn't find reservation to confirm")
	void confirmReservationNotFoundCase() throws Exception {
		PaymentRequestDTO inputDTO = new PaymentRequestDTO(PaymentType.PIX, new BigDecimal("300.00"), reservationId);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/reservations/{id}/payment", (reservationId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (reservationId + 98)));
		
		Reservation savedReservation = repository.findById(reservationId).orElseThrow();
		
		assertEquals(ReservationStatus.PENDENTE, savedReservation.getStatus());
		assertTrue(savedReservation.getPayments().isEmpty());
	}
}
