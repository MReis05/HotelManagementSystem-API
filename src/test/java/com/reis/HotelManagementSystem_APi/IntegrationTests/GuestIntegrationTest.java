package com.reis.HotelManagementSystem_APi.IntegrationTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class GuestIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private GuestRepository repository;
	
	private Long guestId;
	
	@BeforeEach
	void injectObject() {
		Guest g1 = new Guest("John Green", "14462660013","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		
		g1 = this.repository.save(g1);
		
		this.guestId = g1.getId();
	}
	
	@Test
	@DisplayName("Should return all Guests in Database and 200 OK status (End-to-end)")
	void findAllSuccessCase() throws Exception {
		mockMvc.perform(
				get("/guests")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(guestId))
				.andExpect(jsonPath("$[0].name").value("John Green"))
				.andExpect(jsonPath("$[0].cpf").value("14462660013"))
				.andExpect(jsonPath("$[0].birthDate").value("2003-01-05"))
				.andExpect(jsonPath("$[0].address.cep").value("05606-100"));
	}
	
	@Test
	@DisplayName("Should return Guest when finding by existing Id and 200 Ok status (End-to-End)")
	void findByIdSuccessCase() throws Exception {
		mockMvc.perform(
				get("/guests/" + guestId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("John Green"))
				.andExpect(jsonPath("$.cpf").value("14462660013"))
				.andExpect(jsonPath("$.birthDate").value("2003-01-05"))
				.andExpect(jsonPath("$.address.cep").value("05606-100"));
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn't find Guest")
	void findByIdResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				get("/guests/" + (guestId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (guestId + 98)));
	}
}
