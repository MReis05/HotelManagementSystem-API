package com.reis.HotelManagementSystem_APi.IntegrationTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
