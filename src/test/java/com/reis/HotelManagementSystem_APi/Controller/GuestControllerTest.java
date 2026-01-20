package com.reis.HotelManagementSystem_APi.Controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.reis.HotelManagementSystem_APi.controllers.GuestController;
import com.reis.HotelManagementSystem_APi.dto.GuestResponseDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.services.GuestService;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

@WebMvcTest(GuestController.class)
public class GuestControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private GuestService service;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Test
	@DisplayName("Should return 200 OK and a List of Guest")
	void findAllSuccessCase() throws Exception {
		GuestResponseDTO dto = new GuestResponseDTO(createStandardGuest());
		
		when(service.findAll()).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/guests")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].name").value("John Green"))
				.andExpect(jsonPath("$[0].cpf").value("99999999901"))
				.andExpect(jsonPath("$[0].birthDate").value("2003-01-05"));
	}
	
	
	@Test
	@DisplayName("Should return 200 OK status and Object Guest")
	void findByIdSuccessCase() throws Exception {
		GuestResponseDTO dto = new GuestResponseDTO(createStandardGuest());
		
		when(service.findById(1L)).thenReturn(dto);
		
		mockMvc.perform(
				get("/guests/" + 1L)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("John Green"))
				.andExpect(jsonPath("$.cpf").value("99999999901"))
				.andExpect(jsonPath("$.birthDate").value("2003-01-05"));
		
	}
	
	@Test
	@DisplayName("Should return 404 Status when doesn't find a Guest")
	void FindByIdResourceNotFoundCase() throws Exception {
		when(service.findById(99L)).thenThrow(new ResourceNotFoundException(99L));
		
		mockMvc.perform(
				get("/guests/" + 99L)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound());
	}
	
	private Guest createStandardGuest() {
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		return g1;
	}
	
}
