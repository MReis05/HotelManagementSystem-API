package com.reis.HotelManagementSystem_APi.Controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import com.reis.HotelManagementSystem_APi.dto.ReservationSummaryDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.services.ReservationService;

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
				.andExpect(jsonPath("$[0].checkInDate").value("2025-11-25"))
				.andExpect(jsonPath("$[0].checkOutDate").value("2025-11-28"))
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
				.andExpect(jsonPath("$[0].checkInDate").value("2025-11-25"))
				.andExpect(jsonPath("$[0].checkOutDate").value("2025-11-28"))
				.andExpect(jsonPath("$[0].status").value(dto.getStatus().name()))
				.andExpect(jsonPath("$[0].totalValue").value(570.00));
	}
	
	private Reservation createStandardReservation() {
		Reservation rv = new Reservation(LocalDate.of(2025, 11, 25), LocalDate.of(2025, 11, 28), ReservationStatus.PENDENTE);
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
