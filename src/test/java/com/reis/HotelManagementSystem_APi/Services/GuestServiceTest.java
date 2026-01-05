package com.reis.HotelManagementSystem_APi.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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

import com.reis.HotelManagementSystem_APi.dto.AddressDTO;
import com.reis.HotelManagementSystem_APi.dto.GuestRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.GuestResponseDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.services.GuestService;
import com.reis.HotelManagementSystem_APi.services.exceptions.DatabaseException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class GuestServiceTest {

	@Mock
	private GuestRepository repository;
	
	@InjectMocks
	private GuestService service;
	
	@Test
	@DisplayName("Should return a List<GuestResponseDTO> when find objects")
	void findAllSuccessCase() {
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		List<Guest> listExpected = List.of(g1);
		when(repository.findAll()).thenReturn(listExpected);
		
		List<GuestResponseDTO> listReceived = service.findAll();
		
		assertNotNull(listReceived);
		
		assertEquals(1, listReceived.size());
		
		assertEquals(g1.getId(), listReceived.get(0).getId());
		assertEquals(g1.getName(), listReceived.get(0).getName());
		
		verify(repository).findAll();
	}
	
	@Test
	@DisplayName("Should return a GuestResponseDTO when find object")
	void findByIdSuccessCase() {
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(g1));
		
		GuestResponseDTO guestReceived = service.findById(1L);
		
		assertNotNull(guestReceived);
		assertEquals(g1.getId(), guestReceived.getId());
		
		verify(repository).findById(1L);
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
	@DisplayName("Should save Guest and then return a GuestResponseDTO")
	void insertSuccessCase() {
		AddressDTO address = new AddressDTO("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65);
		GuestRequestDTO dto = new GuestRequestDTO("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), address);
		
		when(repository.save(any(Guest.class))).thenAnswer(invocation ->{
			Guest g = invocation.getArgument(0);
			ReflectionTestUtils.setField(g, "id", 1L);
			return g;
		});
		
		GuestResponseDTO guestReceived = service.insert(dto);
		
		assertNotNull(guestReceived);
		assertEquals(1L, guestReceived.getId());
		assertEquals("John Green", guestReceived.getName());
		
		verify(repository).save(any(Guest.class));
	}
	
	@Test
	@DisplayName("Should delete guest when id exists and has no dependencies")
	void deleteSuccessCase() {
		Long guestId = 1L;
		when(repository.existsById(guestId)).thenReturn(true);
		
		service.delete(guestId);
		
		verify(repository).existsById(guestId);
		
		verify(repository, times(1)).deleteById(guestId);
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when id doesn't exist")
	void deleteResourceNotFoundCase() {
		Long guestId = 99L;
		
		when(repository.existsById(guestId)).thenReturn(false);
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(guestId);
		});
		
		assertNotNull(exception);
		assertEquals(ResourceNotFoundException.class, exception.getClass());
		
		verify(repository, never()).deleteById(guestId);
	}
	
	@Test
	@DisplayName("Should throw DatabaseException when deleting guest with dependencies")
	void deleteDataIntegrityViolationCase() {
		Long guestId = 1L;
		
		when(repository.existsById(guestId)).thenReturn(true);
		
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(guestId);
		
		DatabaseException exception = assertThrows(DatabaseException.class, () ->{
			service.delete(1L);
		});
		
		assertNotNull(exception);
		assertEquals(DatabaseException.class, exception.getClass());
		verify(repository, times(1)).deleteById(guestId);
	}
	
	@Test
	@DisplayName("Should update only provided fields and keep others unchanged")
	void updateSuccessCase() {
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(g1));
		
		GuestRequestDTO dto = new GuestRequestDTO();
		dto.setName("John Black");
		AddressDTO address = new AddressDTO();
		address.setStreet("Avenida Morumbi");
		dto.setAddress(address);
		
		when(repository.save(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		GuestResponseDTO g1Received = service.update(1L, dto);
		
		assertNotNull(g1Received);
		
		assertEquals(g1.getId(), g1Received.getId());
		assertEquals("John Black", g1Received.getName());
		assertEquals("Avenida Morumbi", g1Received.getAddress().getStreet());
		
		assertEquals("john@gmail.com", g1Received.getEmail());
		assertEquals("05606-100", g1Received.getAddress().getCep());
	}
	
	@Test
	@DisplayName("Should throw ResourceNotFoundException when updating non-existent id")
	void updateResourceNotFoundCase() {
	    GuestRequestDTO dto = new GuestRequestDTO();
	    
	    when(repository.findById(99L)).thenReturn(Optional.empty());
	    
	    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
	        service.update(99L, dto);
	    });
	    
	    assertNotNull(exception);
	    assertEquals(ResourceNotFoundException.class, exception.getClass());
	    
	    verify(repository, never()).save(any());
	}
}
