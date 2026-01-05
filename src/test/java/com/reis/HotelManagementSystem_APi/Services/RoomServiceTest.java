package com.reis.HotelManagementSystem_APi.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import com.reis.HotelManagementSystem_APi.dto.RoomCreateDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomUpdateDTO;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;
import com.reis.HotelManagementSystem_APi.services.RoomService;
import com.reis.HotelManagementSystem_APi.services.exceptions.DatabaseException;
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
	
	@Test
	@DisplayName("Should return a List<RoomResponseDTO>  when find objects with the right type")
	void findByTypeSuccessCase() {
		Room r1 = createStandardRoom();
		List<Room> listExpected = List.of(r1);
		
		when(repository.findByType(RoomType.SOLTEIRO)).thenReturn(listExpected);
		
		List<RoomResponseDTO> listReceived = service.findByType(RoomType.SOLTEIRO.name());
		
		assertNotNull(listReceived);
		assertEquals(listExpected.get(0).getId(), listReceived.get(0).getId());
		assertEquals(RoomType.SOLTEIRO, listReceived.get(0).getType());
		
		verify(repository).findByType(RoomType.SOLTEIRO);
	}
	
	@Test
	@DisplayName("Should return an empty list when there aren't any objects with type")
	void findByTypeEmptyCase() {
		List<RoomResponseDTO> listReceived = service.findByType("TIPO_INEXISTENTE");
		
		assertNotNull(listReceived);
		assertTrue(listReceived.isEmpty());
		
		verify(repository, never()).findByType(any());
	}
	
	@Test
	@DisplayName("Should return a List<RoomResponseDTO>  when find objects with the right type and status")
	void findByTypeAndStatusSuccessCase() {
		Room r1 = createStandardRoom();
		List<Room> listExpected = List.of(r1);
		
		when(repository.findByTypeAndStatus(RoomType.SOLTEIRO, RoomStatus.DISPONIVEL)).thenReturn(listExpected);
		
		List<RoomResponseDTO> listReceived = service.findByTypeAndStatus(RoomType.SOLTEIRO.name(), RoomStatus.DISPONIVEL.name());
		
		assertNotNull(listReceived);
		assertEquals(listExpected.get(0).getId(), listReceived.get(0).getId());
		assertEquals(RoomType.SOLTEIRO, listReceived.get(0).getType());
		assertEquals(RoomStatus.DISPONIVEL, listReceived.get(0).getStatus());
		
		verify(repository).findByTypeAndStatus(RoomType.SOLTEIRO, RoomStatus.DISPONIVEL);
	}
	
	@Test
	@DisplayName("Should return an empty list when there aren't any objects with type and status")
	void findByTypeAndStatusEmptyCase() {
		List<RoomResponseDTO> listReceived = service.findByTypeAndStatus("TIPO_INEXISTENTE", "STATUS_INEXISTENTE");
		
		assertNotNull(listReceived);
		assertTrue(listReceived.isEmpty());
		
		verify(repository, never()).findByType(any());
	}
	
	@Test
	@DisplayName("Should save Room and then return RoomResponseDTO")
	void insertSuccessCase() {
		RoomCreateDTO dto = new RoomCreateDTO();
		dto.setDescription("Quarto com Ventilador");
		dto.setNumber(1);
		dto.setPricePerNight(new BigDecimal("190.00"));
		dto.setStatus(RoomStatus.DISPONIVEL);
		dto.setType(RoomType.SOLTEIRO);
		
		when(repository.save(any(Room.class))).thenAnswer(invocation -> {
			Room r = invocation.getArgument(0);
			ReflectionTestUtils.setField(r, "id", 1L);
			return r;
		});
		
		RoomResponseDTO roomReceived = service.insert(dto);
		
		assertNotNull(roomReceived);
		assertEquals(1L, roomReceived.getId());
		assertEquals(new BigDecimal("190.00"), roomReceived.getPricePerNight());
		
		verify(repository).save(any(Room.class));
	}
	
	@Test
	@DisplayName("Should update only provided fields and keep others unchanged")
	void updateSuccessCase() {
		Room r1 = createStandardRoom();
		
		when(repository.findById(1L)).thenReturn(Optional.of(r1));
		
		RoomUpdateDTO dto = new RoomUpdateDTO();
		dto.setPricePerNight(new BigDecimal("250.00"));
		dto.setStatus(RoomStatus.MANUTENCAO);
		
		when(repository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		RoomResponseDTO roomReceived = service.update(1L, dto);
		
		assertNotNull(roomReceived);
		assertEquals(new BigDecimal("250.00"), roomReceived.getPricePerNight());
		assertEquals(RoomStatus.MANUTENCAO, roomReceived.getStatus());
		
		assertEquals(1L, roomReceived.getId());
		assertEquals(RoomType.SOLTEIRO, roomReceived.getType());
		
		verify(repository).save(any(Room.class));
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when id doesn't exist")
	void updateResourceNotFoundCase() {
		when(repository.findById(99L)).thenReturn(Optional.empty());
		
		RoomUpdateDTO dto = new RoomUpdateDTO();
		dto.setPricePerNight(new BigDecimal("250.00"));
		dto.setStatus(RoomStatus.MANUTENCAO);
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			service.update(99L, dto);
		});
		
		assertNotNull(exception.getMessage());
		assertEquals(ResourceNotFoundException.class, exception.getClass());
		
		verify(repository).findById(99L);
		verify(repository, never()).save(any());
	}
	
	@Test
	@DisplayName("Should delete room when id exists and has no dependencies")
	void deleteSuccessCase() {
		Long roomId = 1L;
		when(repository.existsById(roomId)).thenReturn(true);
		
		service.delete(roomId);
		
		verify(repository).existsById(roomId);
		
		verify(repository, times(1)).deleteById(roomId);
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when id doesn't exist")
	void deleteResourceNotFoundCase() {
		Long roomId = 99L;
		
		when(repository.existsById(roomId)).thenReturn(false);
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(roomId);
		});
		
		assertNotNull(exception);
		assertEquals(ResourceNotFoundException.class, exception.getClass());
		
		verify(repository, never()).deleteById(roomId);
	}
	
	@Test
	@DisplayName("Should throw DatabaseException when deleting room with dependencies")
	void deleteDataIntegrityViolationCase() {
		Long roomId = 1L;
		
		when(repository.existsById(roomId)).thenReturn(true);
		
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(roomId);
		
		DatabaseException exception = assertThrows(DatabaseException.class, () ->{
			service.delete(1L);
		});
		
		assertNotNull(exception);
		assertEquals(DatabaseException.class, exception.getClass());
		verify(repository, times(1)).deleteById(roomId);
	}
	
	
	private Room createStandardRoom() {
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		ReflectionTestUtils.setField(r1, "id", 1L);
		return r1;
	}
}
