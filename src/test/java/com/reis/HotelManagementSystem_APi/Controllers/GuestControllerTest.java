package com.reis.HotelManagementSystem_APi.Controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import com.reis.HotelManagementSystem_APi.dto.AddressDTO;
import com.reis.HotelManagementSystem_APi.dto.GuestRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.GuestResponseDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.services.GuestService;
import com.reis.HotelManagementSystem_APi.services.exceptions.DatabaseException;
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
				.andExpect(jsonPath("$[0].cpf").value("14462660013"))
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
				.andExpect(jsonPath("$.cpf").value("14462660013"))
				.andExpect(jsonPath("$.birthDate").value("2003-01-05"));
		
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find a Guest")
	void findByIdResourceNotFoundCase() throws Exception {
		Long guestId = 99L;
		when(service.findById(guestId)).thenThrow(new ResourceNotFoundException(guestId));
		
		mockMvc.perform(
				get("/guests/" + guestId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return 201 Created and the location Header")
	void insertSuccessCase() throws Exception {
		AddressDTO address = new AddressDTO("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65);
		GuestRequestDTO inputDTO = new GuestRequestDTO("John Green", "14462660013","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), address);
		
		GuestResponseDTO outputDTO = new GuestResponseDTO(createStandardGuest());
		
		when(service.insert(any(GuestRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/guests")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("John Green"))
				.andExpect(header().exists("Location"));
	}
	
	@Test
	@DisplayName("Should return 200 OK when updating guest")
	void updateSuccessCase() throws Exception {
		AddressDTO address = new AddressDTO("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65);
		GuestRequestDTO inputDTO = new GuestRequestDTO("John Blue", "14462660013","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 02), address);
		
		GuestResponseDTO outputDTO = new GuestResponseDTO(createStandardGuest());
		ReflectionTestUtils.setField(outputDTO, "name", "John Blue");
		
		when(service.update(eq(1L), any(GuestRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/guests/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("John Blue"));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find Guest")
	void updateResourceNotFoundCase() throws Exception {
		Long guestId = 99L;
		
		AddressDTO address = new AddressDTO("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65);
		GuestRequestDTO inputDTO = new GuestRequestDTO("John Blue", "14462660013","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 02), address);
		
		GuestResponseDTO outputDTO = new GuestResponseDTO(createStandardGuest());
		ReflectionTestUtils.setField(outputDTO, "name", "John Blue");
		
		when(service.update(eq(guestId), any())).thenThrow(new ResourceNotFoundException(guestId));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/guests/{id}", guestId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return 204 No Content when deleting Guest")
	void deleteSuccessCase() throws Exception {
		mockMvc.perform(
				delete("/guests/" + 1L)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find Guest")
	void deleteResourceNotFoundCase() throws Exception {
		Long guestId = 99L;
		doThrow(new ResourceNotFoundException(guestId)).when(service).delete(guestId);
		
		mockMvc.perform(
				delete("/guests/" + guestId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when deleting guest with dependencies")
	void deleteIntegrityViolationCase() throws Exception {
		Long guestId = 99L;
		doThrow(new DatabaseException("Integrity Violation")).when(service).delete(guestId);
		
		mockMvc.perform(
				delete("/guests/" + guestId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isBadRequest());
	}
	
	private Guest createStandardGuest() {
		Guest g1 = new Guest("John Green", "14462660013","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		return g1;
	}
	
}
