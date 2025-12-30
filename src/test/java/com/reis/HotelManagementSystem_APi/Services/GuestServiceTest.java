package com.reis.HotelManagementSystem_APi.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.services.GuestService;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class GuestServiceTest {

	@Mock
	private GuestRepository repository;
	
	@InjectMocks
	private GuestService service;
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when id doesn't exist")
	void findByResourceNotFoundCase() {
		when(repository.findById(99L)).thenReturn(Optional.empty());
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(99L);
		});
		
		assertNotNull(exception.getMessage());
		assertEquals(ResourceNotFoundException.class, exception.getClass());
		
		verify(repository).findById(99L);
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
}
