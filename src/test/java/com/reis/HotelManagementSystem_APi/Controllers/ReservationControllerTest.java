package com.reis.HotelManagementSystem_APi.Controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reis.HotelManagementSystem_APi.controllers.ReservationController;
import com.reis.HotelManagementSystem_APi.dto.PaymentRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.PaymentSummaryDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationSummaryDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Payment;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentType;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.services.ReservationService;
import com.reis.HotelManagementSystem_APi.services.exceptions.InvalidActionException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;
import com.reis.HotelManagementSystem_APi.services.exceptions.RoomUnavailableException;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private ReservationService service;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Test
	@DisplayName("Should return 200 Ok and a List of Reservation")
	void findAllSuccessCase() throws Exception {
		ReservationSummaryDTO dto = new ReservationSummaryDTO(createStandardReservation());
		
		when(service.findAll()).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].checkInDate").value("2026-02-01"))
				.andExpect(jsonPath("$[0].checkOutDate").value("2026-02-04"))
				.andExpect(jsonPath("$[0].status").value(dto.getStatus().name()))
				.andExpect(jsonPath("$[0].totalValue").value(570.00));
	}
	
	@Test
	@DisplayName("Should return 200 Ok and a List of Reservation with right status")
	void findByStatusSuccessCase() throws Exception {
		ReservationSummaryDTO dto = new ReservationSummaryDTO(createStandardReservation());
		
		when(service.findByStatus(dto.getStatus().name())).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/reservations?status=" + dto.getStatus().name())
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].checkInDate").value("2026-02-01"))
				.andExpect(jsonPath("$[0].checkOutDate").value("2026-02-04"))
				.andExpect(jsonPath("$[0].status").value(dto.getStatus().name()))
				.andExpect(jsonPath("$[0].totalValue").value(570.00))
				.andExpect(jsonPath("$[0].roomSummaryDTO.status").value(dto.getRoomSummaryDTO().getStatus().name()))
				.andExpect(jsonPath("$[0].guestSummaryDTO.name").value(dto.getGuestSummaryDTO().getName()));
	}
	
	@Test
	@DisplayName("Should return 200 OK and Reservation")
	void findByIdSuccessCase() throws Exception {
		Long reservationId = 1L;
		ReservationResponseDTO dto = new ReservationResponseDTO(createStandardReservation());
		
		when(service.findById(reservationId)).thenReturn(dto);
		
		mockMvc.perform(
				get("/reservations/" + reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(reservationId))
				.andExpect(jsonPath("$.checkInDate").value("2026-02-01"))
				.andExpect(jsonPath("$.checkOutDate").value("2026-02-04"))
				.andExpect(jsonPath("$.status").value(dto.getStatus().name()))
				.andExpect(jsonPath("$.totalValue").value(570.00))
				.andExpect(jsonPath("$.roomSummaryDTO.status").value(dto.getRoomSummaryDTO().getStatus().name()))
				.andExpect(jsonPath("$.guestSummaryDTO.name").value(dto.getGuestSummaryDTO().getName()));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find Reservation")
	void findByIdResourceNotFoundCase() throws Exception {
		Long reservationId = 99L;
		
		when(service.findById(reservationId)).thenThrow(new ResourceNotFoundException(reservationId));
		
		mockMvc.perform(
				get("/reservations/" + reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return 201 Created and the location header")
	void insertSuccessCase() throws Exception {
		ReservationRequestDTO inputDTO = new ReservationRequestDTO(1L, 1L, LocalDate.of(2026, 02, 01), LocalDate.of(2026, 02, 04));
		
		ReservationResponseDTO outputDTO = new ReservationResponseDTO(createStandardReservation());
		
		when(service.insert(any(ReservationRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.checkInDate").value("2026-02-01"))
				.andExpect(jsonPath("$.checkOutDate").value("2026-02-04"))
				.andExpect(jsonPath("$.status").value(outputDTO.getStatus().name()))
				.andExpect(jsonPath("$.totalValue").value(570.00))
				.andExpect(jsonPath("$.roomSummaryDTO.status").value(outputDTO.getRoomSummaryDTO().getStatus().name()))
				.andExpect(jsonPath("$.guestSummaryDTO.name").value(outputDTO.getGuestSummaryDTO().getName()))
				.andExpect(header().exists("Location"));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find Guest or Room")
	void insertResourceNotFoundCase() throws Exception {
		ReservationRequestDTO inputDTO = new ReservationRequestDTO(1L, 1L, LocalDate.of(2026, 02, 01), LocalDate.of(2026, 02, 04));
		
		when(service.insert(any(ReservationRequestDTO.class))).thenThrow(new ResourceNotFoundException(1L));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when room is unavailable")
	void insertRoomUnavailableCase() throws Exception {
		ReservationRequestDTO inputDTO = new ReservationRequestDTO(1L, 1L, LocalDate.of(2026, 02, 01), LocalDate.of(2026, 02, 04));
		
		when(service.insert(any(ReservationRequestDTO.class))).thenThrow(new RoomUnavailableException(1));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/reservations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should return 200 OK when updating Reservation")
	void updateReservationSuccessCase() throws Exception {
		Long reservationId = 1L;
		ReservationRequestDTO inputDTO = new ReservationRequestDTO(1L, 1L, LocalDate.of(2026, 02, 06), LocalDate.of(2026, 02, 10));
		
		ReservationResponseDTO outputDTO = new ReservationResponseDTO(createStandardReservation());
		ReflectionTestUtils.setField(outputDTO, "checkInDate", LocalDate.of(2026, 02, 06));
		ReflectionTestUtils.setField(outputDTO, "checkOutDate", LocalDate.of(2026, 02, 10));
		
		when(service.updateReservation(eq(reservationId), any(ReservationRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/reservations/{id}/update", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.checkInDate").value(outputDTO.getCheckInDate().toString()))
				.andExpect(jsonPath("$.checkOutDate").value(outputDTO.getCheckOutDate().toString()));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find Reservation")
	void updateReservationResourceNotFoundCase() throws Exception {
		Long reservationId = 1L;
		ReservationRequestDTO inputDTO = new ReservationRequestDTO(1L, 1L, LocalDate.of(2026, 02, 06), LocalDate.of(2026, 02, 10));;
		
		when(service.updateReservation(eq(reservationId), any(ReservationRequestDTO.class))).thenThrow(new ResourceNotFoundException(reservationId));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/reservations/{id}/update", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when reservation is already completed or canceled")
	void updateReservationInvalidActionCase() throws Exception {
		Long reservationId = 1L;
		ReservationRequestDTO inputDTO = new ReservationRequestDTO(1L, 1L, LocalDate.of(2026, 02, 06), LocalDate.of(2026, 02, 10));;
		
		when(service.updateReservation(eq(reservationId), any(ReservationRequestDTO.class))).thenThrow(new InvalidActionException("It is not possible to update a completed or canceled reservation"));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/reservations/{id}/update", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should return 200 OK when canceling Reservation")
	void cancelReservationSuccessCase() throws Exception {
		Long reservationId = 1L;
		
		ReservationResponseDTO outputDTO = new ReservationResponseDTO(createStandardReservation());
		ReflectionTestUtils.setField(outputDTO, "status", ReservationStatus.CANCELADA);
		
		when(service.cancelReservation(eq(reservationId))).thenReturn(outputDTO);
		
		mockMvc.perform(
				patch("/reservations/{id}/cancel", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value(ReservationStatus.CANCELADA.name()));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find Reservation")
	void cancelReservationResourceNotFoundCase() throws Exception {
		Long reservationId = 1L;
		
		when(service.cancelReservation(eq(reservationId))).thenThrow(new ResourceNotFoundException(reservationId));
	
		mockMvc.perform(
				patch("/reservations/{id}/cancel", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return 200 OK when confirming Reservation")
	void confirmReservationSuccessCase() throws Exception {
		Long reservationId = 1L;
		Payment payment = new Payment(Instant.now(), PaymentStatus.APROVADO, PaymentType.PIX, new BigDecimal("300.00"), createStandardReservation());
		
		PaymentRequestDTO inputDTO = new PaymentRequestDTO(payment.getType(), payment.getAmount(), payment.getReservation().getId());
		
		ReservationResponseDTO outputDTO = new ReservationResponseDTO(createStandardReservation());
		outputDTO.getPaymentsSummary().add(new PaymentSummaryDTO(payment));
		ReflectionTestUtils.setField(outputDTO, "status", ReservationStatus.CONFIRMADA);
		
		when(service.confirmReservation(eq(reservationId), any(PaymentRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/reservations/{id}/payment", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value(outputDTO.getStatus().name()))
				.andExpect(jsonPath("$.paymentsSummary[0].moment").exists())
				.andExpect(jsonPath("$.paymentsSummary[0].amount").value(300.00))
				.andExpect(jsonPath("$.paymentsSummary[0].status").value(outputDTO.getPaymentsSummary().get(0).getStatus().name()))
				.andExpect(jsonPath("$.paymentsSummary[0].type").value(outputDTO.getPaymentsSummary().get(0).getType().name()));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find Reservation")
	void confirmReservationResourceNotFoundCase() throws Exception {
		Long reservationId = 99L;
		Payment payment = new Payment(Instant.now(), PaymentStatus.APROVADO, PaymentType.PIX, new BigDecimal("300.00"), createStandardReservation());
		
		PaymentRequestDTO inputDTO = new PaymentRequestDTO(payment.getType(), payment.getAmount(), payment.getReservation().getId());
		
		when(service.confirmReservation(eq(reservationId), any(PaymentRequestDTO.class))).thenThrow(new ResourceNotFoundException(reservationId));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/reservations/{id}/payment", reservationId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound());
	}
	
	private Reservation createStandardReservation() {
		Reservation rv = new Reservation(LocalDate.of(2026, 02, 01), LocalDate.of(2026, 02, 04), ReservationStatus.PENDENTE);
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		ReflectionTestUtils.setField(r1, "id", 1L);
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		rv.setId(1L);
		rv.setTotalValue(new BigDecimal("570.00"));
		rv.setRoom(r1);
		rv.setGuest(g1);
		return rv;
	}
}
