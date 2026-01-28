package com.reis.HotelManagementSystem_APi.Controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.reis.HotelManagementSystem_APi.controllers.StayController;
import com.reis.HotelManagementSystem_APi.dto.IncidentalRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.IncidentalResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.PaymentRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.PaymentResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.StayRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.StayResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.StaySummaryDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Incidental;
import com.reis.HotelManagementSystem_APi.entities.Payment;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.Stay;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentType;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.services.StayService;
import com.reis.HotelManagementSystem_APi.services.exceptions.CheckInDateException;
import com.reis.HotelManagementSystem_APi.services.exceptions.InvalidActionException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

@WebMvcTest(StayController.class)
public class StayControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private StayService service;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Test
	@DisplayName("Should return 200 OK and a List of Stay")
	void findAllSuccessCase() throws Exception {
		StaySummaryDTO dto = new StaySummaryDTO(createStandardStay());
		
		when(service.findAll()).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/stays")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].checkInDate").value(dto.getCheckInDate().toString()))
				.andExpect(jsonPath("$[0].totalValue").value(770.00))
				.andExpect(jsonPath("$[0].guestSummaryDTO.name").value("John Green"));
	}
	
	@Test
	@DisplayName("Should return 200 OK and Stay")
	void findByIdSuccessCase() throws Exception {
		Long stayId = 1L;
		
		StayResponseDTO dto = new StayResponseDTO(createStandardStay());
		
		when(service.findById(stayId)).thenReturn(dto);
		
		mockMvc.perform(
				get("/stays/" + stayId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.reservationId").value(1L))
				.andExpect(jsonPath("$.checkInDate").value(dto.getCheckInDate().toString()))
				.andExpect(jsonPath("$.totalValue").value(770.00))
				.andExpect(jsonPath("$.guestSummaryDTO.name").value("John Green"))
				.andExpect(jsonPath("$.incidentalsList[0].moment").value(dto.getIncidentalsList().get(0).getMoment().toString()))
				.andExpect(jsonPath("$.incidentalsList[0].total").value(10.00));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find Stay")
	void findByIdResourceNotFoundCase() throws Exception {
		Long stayId = 99L;
		
		when(service.findById(stayId)).thenThrow(new ResourceNotFoundException(stayId));
		
		mockMvc.perform(
				get("/stays/" + stayId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return 201 Created and the location header")
	void checkInSuccessCase() throws Exception {
		StayRequestDTO inputDTO = new StayRequestDTO();
		inputDTO.setReservationId(1L);
		inputDTO.setCheckInDate(LocalDateTime.now().withNano(0));
		
		StayResponseDTO outputDTO = new StayResponseDTO(createStandardStay());
		outputDTO.getIncidentalsList().clear();
		ReflectionTestUtils.setField(outputDTO, "totalValue", new BigDecimal("760.00"));
		
		when(service.checkIn(any(StayRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.reservationId").value(1L))
				.andExpect(jsonPath("$.checkInDate").value(outputDTO.getCheckInDate().toString()))
				.andExpect(jsonPath("$.totalValue").value(760.00))
				.andExpect(jsonPath("$.guestSummaryDTO.name").value("John Green"))
				.andExpect(header().exists("Location"));
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when Stay checkInDate is not equal with Reservation CheckInDate")
	void checkInDateExceptionCase() throws Exception {
		StayRequestDTO inputDTO = new StayRequestDTO();
		inputDTO.setCheckInDate(LocalDateTime.now().withNano(0));
		
		StayResponseDTO outputDTO = new StayResponseDTO(createStandardStay());
		
		when(service.checkIn(any(StayRequestDTO.class))).thenThrow(new CheckInDateException(inputDTO.getCheckInDate(), outputDTO.getCheckInDate().toLocalDate()));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should return 200 OK when CheckOut is completed")
	void checkOutSuccessCase() throws Exception {
		Long stayId = 1L;
		
		StayResponseDTO dto = new StayResponseDTO(createStandardStay());
		ReflectionTestUtils.setField(dto, "checkOutDate", LocalDateTime.now().plusDays(4L).withNano(0));
		
		when(service.checkOut(stayId)).thenReturn(dto);
		
		mockMvc.perform(
				patch("/stays/{id}/checkout", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.checkOutDate").value(dto.getCheckOutDate().toString()));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn's find Stay")
	void checkOutResourceNotFoundCase() throws Exception {
		Long stayId = 99L;
		
		when(service.checkOut(stayId)).thenThrow(new ResourceNotFoundException(stayId));
		
		mockMvc.perform(
				patch("/stays/{id}/checkout", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound());
	}
	

	@Test
	@DisplayName("Should return 400 Bad Request when Stay is already completed")
	void checkOutInvalidActionCase() throws Exception {
		Long stayId = 99L;
		
		when(service.checkOut(stayId)).thenThrow(new InvalidActionException("Stay is already completed"));
		
		mockMvc.perform(
				patch("/stays/{id}/checkout", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should return 201 Created and the location header")
	void addIncidentalSuccessCase() throws Exception {
		Long stayId = 1L;
		IncidentalRequestDTO inputDTO = new IncidentalRequestDTO("Coca-Cola", 2, new BigDecimal("5.00"), LocalDateTime.now().plusHours(4L).withNano(0));
		
		IncidentalResponseDTO outputDTO = new IncidentalResponseDTO(createStandardStay().getIncidentals().get(0));
		
		when(service.addIncidental(eq(stayId), any(IncidentalRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays/{id}/incidental", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value(outputDTO.getName()))
				.andExpect(jsonPath("$.quantity").value(outputDTO.getQuantity()))
				.andExpect(jsonPath("$.price").value(5.00))
				.andExpect(jsonPath("$.total").value(10.00))
				.andExpect(jsonPath("$.moment").value(outputDTO.getMoment().toString()))
				.andExpect(header().exists("Location"));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find Stay")
	void addIncidentalResourceNotFoundCase() throws Exception {
		Long stayId = 99L;
		IncidentalRequestDTO inputDTO = new IncidentalRequestDTO("Coca-Cola", 2, new BigDecimal("5.00"), LocalDateTime.now().plusHours(4L).withNano(0));
		
		when(service.addIncidental(eq(stayId), any(IncidentalRequestDTO.class))).thenThrow(new ResourceNotFoundException(stayId));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays/{id}/incidental", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when trying add an incidental in a finished Stay")
	void addIncidentalInvalidActionCase() throws Exception {
		Long stayId = 99L;
		IncidentalRequestDTO inputDTO = new IncidentalRequestDTO("Coca-Cola", 2, new BigDecimal("5.00"), LocalDateTime.now().plusHours(4L).withNano(0));
		
		when(service.addIncidental(eq(stayId), any(IncidentalRequestDTO.class))).thenThrow(new InvalidActionException("Stay is already completed"));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays/{id}/incidental", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should return 201 Created and the location header")
	void makePaymentSuccessCase() throws Exception {
		Long id = 1L;
		Payment p = new Payment(Instant.now(), PaymentStatus.APROVADO, PaymentType.CARTAO_DE_CREDITO, new BigDecimal("300.00"), createStandardStay().getReservation());
		ReflectionTestUtils.setField(p, "id", id);
		
		PaymentRequestDTO inputDTO = new PaymentRequestDTO(PaymentType.CARTAO_DE_CREDITO, new BigDecimal("300.00"), id);
		
		PaymentResponseDTO outputDTO = new PaymentResponseDTO(p);
		
		when(service.makePayment(eq(id), any(PaymentRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays/{id}/payment", id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.moment").exists())
				.andExpect(jsonPath("$.amount").value(300.00))
				.andExpect(jsonPath("$.status").value(outputDTO.getStatus().name()))
				.andExpect(jsonPath("$.type").value(outputDTO.getType().name()))
				.andExpect(jsonPath("$.reservationId").exists())
				.andExpect(header().exists("Location"));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find Stay")
	void makePaymentResourceNotFoundCase() throws Exception {
		Long id = 99L;
	
		PaymentRequestDTO inputDTO = new PaymentRequestDTO(PaymentType.CARTAO_DE_CREDITO, new BigDecimal("300.00"), id);
		
		when(service.makePayment(eq(id), any(PaymentRequestDTO.class))).thenThrow(new ResourceNotFoundException(id));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays/{id}/payment", id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound());
	}
	
	private Stay createStandardStay() {
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.OCUPADO, RoomType.SOLTEIRO);
		ReflectionTestUtils.setField(r1, "id", 1L);
		Reservation rv1 = new Reservation(LocalDate.now(), LocalDate.now().plusDays(4), ReservationStatus.EM_CURSO);
		rv1.setId(1L);
		rv1.setGuest(g1);
		rv1.setRoom(r1);
		rv1.setTotalValue(new BigDecimal("760.00"));
		Stay s = new Stay();
		ReflectionTestUtils.setField(s, "id", 1L);
		s.setCheckInDate(LocalDateTime.now().withNano(0));
		s.setReservation(rv1);
		Incidental i = new Incidental("Coca-Cola", 2, new BigDecimal("5.00"), LocalDateTime.now().plusHours(4L).withNano(0), s);
		ReflectionTestUtils.setField(i, "id", 1L);
		s.getIncidentals().add(i);
		return s;
	}
}
	
