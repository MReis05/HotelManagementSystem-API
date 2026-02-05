package com.reis.HotelManagementSystem_APi.IntegrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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
import com.reis.HotelManagementSystem_APi.dto.RoomCreateDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomUpdateDTO;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RoomIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private RoomRepository repository;
	
	private Long roomId;
	
	@BeforeEach
	void injectRoom() {
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		
		r1 = this.repository.save(r1);
		
		this.roomId = r1.getId();
	}
	
	@Test
	@DisplayName("Should return all Rooms in Database and 200 OK status (End-to-End")
	void findAllSuccessCase() throws Exception {
		mockMvc.perform(
				get("/rooms")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(roomId))
				.andExpect(jsonPath("$[0].number").value(1))
				.andExpect(jsonPath("$[0].pricePerNight").value(190.00))
				.andExpect(jsonPath("$[0].description").value("Quarto com Ventilador"))
				.andExpect(jsonPath("$[0].status").value(RoomStatus.DISPONIVEL.name()))
				.andExpect(jsonPath("$[0].type").value(RoomType.SOLTEIRO.name()));
	}
	
	@Test
	@DisplayName("Should return a List of Room with right status and type and 200 OK status")
	void findByStatusAndTypeSuccessCase() throws Exception {
		mockMvc.perform(
				get("/rooms?type=" + RoomType.SOLTEIRO.name() + "&status=" + RoomStatus.DISPONIVEL.name())
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(roomId))
				.andExpect(jsonPath("$[0].number").value(1))
				.andExpect(jsonPath("$[0].pricePerNight").value(190.00))
				.andExpect(jsonPath("$[0].description").value("Quarto com Ventilador"))
				.andExpect(jsonPath("$[0].status").value(RoomStatus.DISPONIVEL.name()))
				.andExpect(jsonPath("$[0].type").value(RoomType.SOLTEIRO.name()));
	}
	
	@Test
	@DisplayName("Should return a List of Room with right status and 200 OK status (End-to-End)")
	void findByStatusSuccessCase() throws Exception {
		mockMvc.perform(
				get("/rooms?status=" + RoomStatus.DISPONIVEL.name())
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(roomId))
				.andExpect(jsonPath("$[0].number").value(1))
				.andExpect(jsonPath("$[0].pricePerNight").value(190.00))
				.andExpect(jsonPath("$[0].description").value("Quarto com Ventilador"))
				.andExpect(jsonPath("$[0].status").value(RoomStatus.DISPONIVEL.name()))
				.andExpect(jsonPath("$[0].type").value(RoomType.SOLTEIRO.name()));
	}
	
	@Test
	@DisplayName("Should return empty list when filtering by non-existent status")
	void findByStatusNotFoundCase() throws Exception {
		mockMvc.perform(
				get("/rooms?status=" + RoomStatus.MANUTENCAO.name())
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isEmpty());
	}
	
	@Test
	@DisplayName("Should return a List of Room with right type and 200 OK status (End-to-End)")
	void findByTypeSuccessCase() throws Exception {
		mockMvc.perform(
				get("/rooms?type=" + RoomType.SOLTEIRO.name())
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(roomId))
				.andExpect(jsonPath("$[0].number").value(1))
				.andExpect(jsonPath("$[0].pricePerNight").value(190.00))
				.andExpect(jsonPath("$[0].description").value("Quarto com Ventilador"))
				.andExpect(jsonPath("$[0].status").value(RoomStatus.DISPONIVEL.name()))
				.andExpect(jsonPath("$[0].type").value(RoomType.SOLTEIRO.name()));
	}
	
	@Test
	@DisplayName("Should return an empty list when filtering by non-existent type")
	void findByTypeNotFoundCase() throws Exception {
		mockMvc.perform(
				get("/rooms?type=" + RoomType.CASAL.name())
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isEmpty());
	}
	
	@Test
	@DisplayName("Should return Room when finding by existing Id and 200 Ok status (End-to-End)")
	void findByIdSuccessCase() throws Exception {
		mockMvc.perform(
				get("/rooms/{id}", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(roomId))
				.andExpect(jsonPath("$.number").value(1))
				.andExpect(jsonPath("$.pricePerNight").value(190.00))
				.andExpect(jsonPath("$.description").value("Quarto com Ventilador"))
				.andExpect(jsonPath("$.status").value(RoomStatus.DISPONIVEL.name()))
				.andExpect(jsonPath("$.type").value(RoomType.SOLTEIRO.name()));
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn't find Room")
	void findByIdResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				get("/rooms/{id}", (roomId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (roomId + 98)));
	}
	
	@Test
	@DisplayName("Should create and save a Room in Database and return 201 created status")
	void insertSuccessCase() throws Exception {
		RoomCreateDTO inputDTO = new RoomCreateDTO(45, new BigDecimal("250.00"), "Quarto com Ar-Condicionado", RoomStatus.DISPONIVEL, RoomType.CASAL);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/rooms")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.number").value(45))
				.andExpect(jsonPath("$.pricePerNight").value(250.00))
				.andExpect(jsonPath("$.description").value("Quarto com Ar-Condicionado"))
				.andExpect(jsonPath("$.status").value(RoomStatus.DISPONIVEL.name()))
				.andExpect(jsonPath("$.type").value(RoomType.CASAL.name()));
		
		Room savedRoom = repository.findAll().stream().filter(g -> g.getNumber().equals(45))
				         .findFirst().orElseThrow(() -> new AssertionError("Room não encontrado"));
		
		assertEquals(2, repository.count());
		assertEquals(45, savedRoom.getNumber());
		assertEquals(new BigDecimal("250.00"), savedRoom.getPricePerNight());
		assertEquals("Quarto com Ar-Condicionado", savedRoom.getDescription());
		assertEquals(RoomStatus.DISPONIVEL, savedRoom.getStatus());
		assertEquals(RoomType.CASAL, savedRoom.getType());
	}
	
	@Test
	@DisplayName("Should return 422 Unprocessable Entity with Validation Errors")
	void insertWithInvalidFieldsCase() throws Exception {
		RoomCreateDTO inputDTO = new RoomCreateDTO();
		inputDTO.setNumber(45);
		inputDTO.setStatus(RoomStatus.DISPONIVEL);
		inputDTO.setType(RoomType.CASAL);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/rooms")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422))
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[?(@.fieldName == 'pricePerNight')]").exists())
	            .andExpect(jsonPath("$.errors[?(@.fieldName == 'description')]").exists());
		
		assertEquals(1, repository.count());
	}
	
	@Test
	@DisplayName("Should update Room and return 200 OK status (End-to-End)")
	void updateSuccessCase() throws Exception {
		RoomUpdateDTO inputDTO = new RoomUpdateDTO();
		inputDTO.setPricePerNight(new BigDecimal("230.00"));
		inputDTO.setDescription("Quarto com Ar-Condicionado");
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/rooms/{id}", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(roomId))
				.andExpect(jsonPath("$.number").value(1))
				.andExpect(jsonPath("$.pricePerNight").value(230.00))
				.andExpect(jsonPath("$.description").value("Quarto com Ar-Condicionado"))
				.andExpect(jsonPath("$.status").value(RoomStatus.DISPONIVEL.name()))
				.andExpect(jsonPath("$.type").value(RoomType.SOLTEIRO.name()));
		
		Room savedRoom = repository.findById(roomId).orElseThrow();

		assertEquals(1, repository.count());
		assertEquals(1, savedRoom.getNumber());
		assertEquals(new BigDecimal("230.00"), savedRoom.getPricePerNight());
		assertEquals("Quarto com Ar-Condicionado", savedRoom.getDescription());
		assertEquals(RoomStatus.DISPONIVEL, savedRoom.getStatus());
		assertEquals(RoomType.SOLTEIRO, savedRoom.getType());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn't find Room")
	void updateResourceNotFoundCase() throws Exception {
		RoomUpdateDTO inputDTO = new RoomUpdateDTO();
		inputDTO.setPricePerNight(new BigDecimal("230.00"));
		inputDTO.setDescription("Quarto com Ar-Condicionado");
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/rooms/{id}", (roomId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (roomId + 98)));
		
		Room savedRoom = repository.findAll().stream().filter(g -> g.getNumber().equals(1))
		         .findFirst().orElseThrow(() -> new AssertionError("Room não encontrado"));

		assertEquals(1, repository.count());
		assertEquals(new BigDecimal("190.00"), savedRoom.getPricePerNight());
		assertEquals("Quarto com Ventilador", savedRoom.getDescription());
	}
	
	@Test
	@DisplayName("Should delete Room and return 204 No Content (End-to-End")
	void deleteSuccessCase() throws Exception {
		mockMvc.perform(
				delete("/rooms/{id}", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNoContent());
		
		assertEquals(0, repository.count());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn't find Room")
	void deleteResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				delete("/rooms/{id}", (roomId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound());
		
		assertEquals(1, repository.count());
	}
}
