package com.reis.HotelManagementSystem_APi.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.reis.HotelManagementSystem_APi.dto.RoomResponseDTO;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;
import com.reis.HotelManagementSystem_APi.services.RoomService;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {
	
	@Mock
	private RoomRepository repository;
	
	@InjectMocks
	private RoomService service;
	
	@Test
	@DisplayName("Should return a List<RoomResponseDTO> when find objects")
	void findAllSuccessCase() {
		Room r1 = createStandardRoom();
		ReflectionTestUtils.setField(r1, "id", 1L);
		List<Room> listExpected = List.of(r1);
		
		when(repository.findAll()).thenReturn(listExpected);
		
		List<RoomResponseDTO> listReceived = service.findAll();
		
		assertNotNull(listReceived);
		assertEquals(1, listReceived.size());
		assertEquals(1L, listReceived.get(0).getId());
		
		verify(repository).findAll();
	}
	
	@Test
	@DisplayName("Should return RoomResponseDTO when find object")
	void findByIdSuccessCase() {
		Room roomExpected = createStandardRoom();
		
		when(repository.findById(1L)).thenReturn(Optional.of(roomExpected));
		
		RoomResponseDTO roomReceived = service.findById(1L);
		
		assertNotNull(roomReceived);
		assertEquals(roomExpected.getId(), roomReceived.getId());
		
		verify(repository).findById(1l);
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when id doesn't exist")
	void findByIdResourceNotFoundCase() {
		when(repository.findById(99L)).thenReturn(Optional.empty());
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(99L);
		});
		
		assertNotNull(exception.getMessage());
		assertEquals(ResourceNotFoundException.class, exception.getClass());
		
		verify(repository).findById(99L);
	}
	
	@Test
	@DisplayName("Should return a List<RoomResponseDTO>  when find objects with the right status")
	void findByStatusSuccessCase() {
		Room r1 = createStandardRoom();
		List<Room> listExpected = List.of(r1);
		
		when(repository.findByStatus(RoomStatus.DISPONIVEL)).thenReturn(listExpected);
		
		List<RoomResponseDTO> listReceived = service.findByStatus(RoomStatus.DISPONIVEL.name());
		
		assertNotNull(listReceived);
		assertEquals(listExpected.get(0).getId(), listReceived.get(0).getId());
		assertEquals(RoomStatus.DISPONIVEL, listReceived.get(0).getStatus());
		
		verify(repository).findByStatus(RoomStatus.DISPONIVEL);
	}
	
	@Test
	@DisplayName("Should return an empty list when there aren't any objects with status")
	void findByStatusEmptyCase() {
		List<RoomResponseDTO> listReceived = service.findByStatus("STATUS_INEXISTENTE");
		
		assertNotNull(listReceived);
		assertTrue(listReceived.isEmpty());
		
		verify(repository, never()).findByStatus(any());
	}
	
	private Room createStandardRoom() {
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		ReflectionTestUtils.setField(r1, "id", 1L);
		return r1;
	}
}
