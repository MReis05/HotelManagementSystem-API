package com.reis.HotelManagementSystem_APi.IntegrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.reis.HotelManagementSystem_APi.dto.AddressDTO;
import com.reis.HotelManagementSystem_APi.dto.GuestRequestDTO;
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
				get("/guests/{id}", guestId)
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
				get("/guests/{id}", (guestId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (guestId + 98)));
	}
	
	@Test
	@DisplayName("Should create and save Guest in Database and return 201 Created status (End-to-End")
	void insertSuccessCase() throws Exception {
		AddressDTO address = new AddressDTO("05606-100", "São Paulo", "São Paulo", "Morumbi", "Av.Morumbi", 102);
		GuestRequestDTO inputDTO = new GuestRequestDTO("Paul Black", "011.180.880-42","paul@gmail.com", "21989091214", LocalDate.of(2000, 1, 11), address);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/guests")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Paul Black"))
				.andExpect(jsonPath("$.cpf").value("011.180.880-42"))
				.andExpect(jsonPath("$.birthDate").value("2000-01-11"))
				.andExpect(jsonPath("$.address.cep").value("05606-100"));
		
		Guest savedGuest = repository.findAll().stream().filter(g -> g.getCpf().equals("011.180.880-42"))
				.findFirst().orElseThrow(() -> new AssertionError("Guest não encontrado"));
		
		assertEquals(2, repository.count());
		assertEquals("Paul Black", savedGuest.getName());
		assertEquals("011.180.880-42", savedGuest.getCpf());
		assertEquals("paul@gmail.com", savedGuest.getEmail());
		assertEquals("21989091214", savedGuest.getPhone());
		assertEquals(LocalDate.of(2000, 1, 11), savedGuest.getBirthDate());
		assertEquals("05606-100", savedGuest.getAddress().getCep());
		assertEquals("São Paulo", savedGuest.getAddress().getUf());
		assertEquals("São Paulo", savedGuest.getAddress().getCity());
		assertEquals("Morumbi", savedGuest.getAddress().getNeighborhood());
		assertEquals("Av.Morumbi", savedGuest.getAddress().getStreet());
		assertEquals(102, savedGuest.getAddress().getHouseNumber());
	}
	
	@Test
	@DisplayName("Should return 422 Unprocessable Entity with Validation Errors")
	void insertWithInvalidFieldsCase() throws Exception {
		AddressDTO address = new AddressDTO("05606", "São Paulo", "São Paulo", "Morumbi", "Av.Morumbi", 102);
		GuestRequestDTO inputDTO = new GuestRequestDTO("Paul Black", "011.180.88","paul@gmail.com", "21989091214", LocalDate.of(2000, 1, 11), address);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/guests")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422))
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.errors").isArray())
	            .andExpect(jsonPath("$.errors[?(@.fieldName == 'cpf')]").exists())
	            .andExpect(jsonPath("$.errors[?(@.fieldName == 'address.cep')]").exists());
		
		assertEquals(1, repository.count());
	}
	
	@Test
	@DisplayName("Should update a Guest and return 200 OK status (End-to-End)")
	void updateSuccessCase() throws Exception {
		AddressDTO address = new AddressDTO();
		address.setStreet("Av.Morumbi");
		GuestRequestDTO inputDTO = new GuestRequestDTO();
		inputDTO.setName("John Black");
		inputDTO.setEmail("johnBlack@gmail.com");
		inputDTO.setAddress(address);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/guests/{id}", guestId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(guestId))
				.andExpect(jsonPath("$.name").value("John Black"))
				.andExpect(jsonPath("$.email").value("johnBlack@gmail.com"))
				.andExpect(jsonPath("$.address.street").value("Av.Morumbi"));
		
		Guest savedGuest = repository.findById(guestId).orElseThrow();
		
		assertEquals(1, repository.count());
		assertEquals(guestId, savedGuest.getId());
		assertEquals("John Black", savedGuest.getName());
		assertEquals("14462660013", savedGuest.getCpf());
		assertEquals("johnBlack@gmail.com", savedGuest.getEmail());
		assertEquals("779118298282", savedGuest.getPhone());
		assertEquals(LocalDate.of(2003, 1, 05), savedGuest.getBirthDate());
		assertEquals("05606-100", savedGuest.getAddress().getCep());
		assertEquals("São Paulo", savedGuest.getAddress().getUf());
		assertEquals("São Paulo", savedGuest.getAddress().getCity());
		assertEquals("Morumbi", savedGuest.getAddress().getNeighborhood());
		assertEquals("Av.Morumbi", savedGuest.getAddress().getStreet());
		assertEquals(65, savedGuest.getAddress().getHouseNumber());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn't find Guest")
	void updateResourceNotFoundCase() throws Exception {
		AddressDTO address = new AddressDTO();
		address.setStreet("Av.Morumbi");
		GuestRequestDTO inputDTO = new GuestRequestDTO();
		inputDTO.setName("John Black");
		inputDTO.setEmail("johnBlack@gmail.com");
		inputDTO.setAddress(address);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/guests/{id}", (guestId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (guestId + 98)));
		
		Guest savedGuest = repository.findAll().stream().filter(g -> g.getCpf().equals("14462660013"))
				.findFirst().orElseThrow(() -> new AssertionError("Guest não encontrado"));
		
		assertEquals("John Green", savedGuest.getName());
		assertEquals("john@gmail.com", savedGuest.getEmail());
		assertEquals("blala", savedGuest.getAddress().getStreet());
	}
	
	@Test
	@DisplayName("Should delete Guest and return 204 No Content (End-to-End)")
	void deleteSuccessCase() throws Exception {
		mockMvc.perform(
				delete("/guests/{id}", guestId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNoContent());
		
		assertEquals(0, repository.count());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn't find Guest")
	void deleteResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				delete("/guests/{id}", (guestId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound());
		
		assertEquals(1, repository.count());
	}
}
