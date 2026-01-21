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

import java.math.BigDecimal;
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
import com.reis.HotelManagementSystem_APi.controllers.RoomController;
import com.reis.HotelManagementSystem_APi.dto.RoomCreateDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomUpdateDTO;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.services.RoomService;
import com.reis.HotelManagementSystem_APi.services.exceptions.DatabaseException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

@WebMvcTest(RoomController.class)
public class RoomControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private RoomService service;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Test
	@DisplayName("Should return 200 OK and a List of Rooms")
	void findAllSuccessCase() throws Exception {
		RoomResponseDTO dto = new RoomResponseDTO(createStandardRoom());
		
		when(service.findAll()).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/rooms")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].pricePerNight").value(190.00))
				.andExpect(jsonPath("$[0].status").value(dto.getStatus().name()))
				.andExpect(jsonPath("$[0].type").value(dto.getType().name()));			
	}
	
	@Test
	@DisplayName("Should return 200 OK and a List of Rooms with right type and status")
	void findByStatusAndTypeCase() throws Exception {
		RoomResponseDTO dto = new RoomResponseDTO(createStandardRoom());
		
		when(service.findByTypeAndStatus(dto.getType().name(), dto.getStatus().name())).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/rooms?type=" + dto.getType().name() + "&status=" + dto.getStatus().name())
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].pricePerNight").value(190.00))
				.andExpect(jsonPath("$[0].status").value(dto.getStatus().name()))
				.andExpect(jsonPath("$[0].type").value(dto.getType().name()));	
	}
	
	@Test
	@DisplayName("Should return 200 OK and a List of Rooms with right status")
	void findByStatusCase() throws Exception {
		RoomResponseDTO dto = new RoomResponseDTO(createStandardRoom());
		
		when(service.findByStatus(dto.getStatus().name())).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/rooms?status=" + dto.getStatus().name())
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].pricePerNight").value(190.00))
				.andExpect(jsonPath("$[0].status").value(dto.getStatus().name()));	
	}
	
	@Test
	@DisplayName("Should return 200 OK and a List of Rooms with right type")
	void findByTypeCase() throws Exception {
		RoomResponseDTO dto = new RoomResponseDTO(createStandardRoom());
		
		when(service.findByType(dto.getType().name())).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/rooms?type=" + dto.getType().name())
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].pricePerNight").value(190.00))
				.andExpect(jsonPath("$[0].type").value(dto.getType().name()));	
	}
	
	@Test
	@DisplayName("Should return 200 Ok and Room")
	void findByIdSuccessCase() throws Exception {
		Long roomId = 1L;
		RoomResponseDTO dto = new RoomResponseDTO(createStandardRoom());
		
		when(service.findById(roomId)).thenReturn(dto);
		
		mockMvc.perform(
				get("/rooms/" + roomId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(roomId))
				.andExpect(jsonPath("$.pricePerNight").value(190.00))
				.andExpect(jsonPath("$.status").value(dto.getStatus().name()))
				.andExpect(jsonPath("$.type").value(dto.getType().name()));	
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find a Room")
	void findByIdResourceNotFoundCase() throws Exception {
		Long roomId = 99L;
		
		when(service.findById(roomId)).thenThrow(new ResourceNotFoundException(roomId));
		
		mockMvc.perform(
				get("/rooms/" + roomId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound());
		
	}
	
	@Test
	@DisplayName("Should return 201 Created and the location header")
	void insertSuccessCase() throws Exception {
		RoomCreateDTO inputDTO = new RoomCreateDTO(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		
		RoomResponseDTO outputDTO = new RoomResponseDTO(createStandardRoom());
		
		when(service.insert(any(RoomCreateDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/rooms")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.pricePerNight").value(190.00))
				.andExpect(jsonPath("$.status").value(inputDTO.getStatus().name()))
				.andExpect(jsonPath("$.type").value(inputDTO.getType().name()))
				.andExpect(header().exists("Location"));
	}
	
	@Test
	@DisplayName("Should return 200 Ok when updating Room")
	void updateSuccessCase() throws Exception {
		Long roomId = 1L;
		RoomUpdateDTO inputDTO = new RoomUpdateDTO();
		inputDTO.setPricePerNight(new BigDecimal("250.00"));
		inputDTO.setStatus(RoomStatus.MANUTENCAO);
		
		RoomResponseDTO outputDTO = new RoomResponseDTO(createStandardRoom());
		outputDTO.setPricePerNight(new BigDecimal("250.00"));
		outputDTO.setStatus(RoomStatus.MANUTENCAO);
		
		when(service.update(eq(roomId), any(RoomUpdateDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/rooms/{id}", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(roomId))
				.andExpect(jsonPath("$.pricePerNight").value(250.00))
				.andExpect(jsonPath("$.status").value(RoomStatus.MANUTENCAO.name()));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find Room")
	void updateResourceNotFoundCase() throws Exception {
		Long roomId = 99L;
		RoomUpdateDTO inputDTO = new RoomUpdateDTO();
		inputDTO.setPricePerNight(new BigDecimal("250.00"));
		inputDTO.setStatus(RoomStatus.MANUTENCAO);
		
		when(service.update(eq(roomId), any(RoomUpdateDTO.class))).thenThrow(new ResourceNotFoundException(roomId));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/rooms/{id}", roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return 204 No Content when deleting Room")
	void deleteSuccessCase() throws Exception {
		mockMvc.perform(
				delete("/rooms/" + 1L)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find Room")
	void deleteResourceNotFoundCase() throws Exception {
		Long roomId = 99L;
		
		doThrow(new ResourceNotFoundException(roomId)).when(service).delete(roomId);
		
		mockMvc.perform(
				delete("/rooms/" + roomId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when deleting Room with dependencies")
	void deleteIntegrityViolationCase() throws Exception {
		Long roomId = 99L;
		
		doThrow(new DatabaseException("Integrity Violation")).when(service).delete(roomId);
		
		mockMvc.perform(
				delete("/rooms/" + roomId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isBadRequest());
	}
	
	private Room createStandardRoom() {
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		ReflectionTestUtils.setField(r1, "id", 1L);
		return r1;
	}

}
