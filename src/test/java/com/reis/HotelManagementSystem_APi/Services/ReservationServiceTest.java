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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.reis.HotelManagementSystem_APi.dto.ReservationRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationSummaryDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.repositories.PaymentRepository;
import com.reis.HotelManagementSystem_APi.repositories.ReservationRepository;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;
import com.reis.HotelManagementSystem_APi.services.ReservationService;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

	@Mock
	private ReservationRepository repository;
	
	@Mock
	private GuestRepository guestRepository;
	
	@Mock
	private RoomRepository roomRepository;
	
	@Mock
	private PaymentRepository paymentRepository;
	
	@InjectMocks
	private ReservationService service;
	
	@Test
	@DisplayName("Should return  List<ReservationSummaryDTO> when find objects with the right status")
	void findByStatusSuccessCase() {
		Reservation rv = new Reservation(LocalDate.of(2025, 11, 25), LocalDate.of(2025, 11, 28), ReservationStatus.CONFIRMADA);
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		rv.setId(1l);
		rv.setTotalValue(new BigDecimal("570.00"));
		rv.setRoom(r1);
		rv.setGuest(g1);
		List<Reservation> listExpected = List.of(rv);
		when(repository.findByStatus(ReservationStatus.CONFIRMADA)).thenReturn(listExpected);
		
		List<ReservationSummaryDTO> listReceveid = service.findByStatus(ReservationStatus.CONFIRMADA.name());
		
		assertNotNull(listReceveid);
		
		assertEquals(1, listReceveid.size());
		
		assertEquals(rv.getId(), listReceveid.get(0).getId());
		assertEquals(rv.getTotalValue(), listReceveid.get(0).getTotalValue());
		assertEquals(rv.getStatus(), listReceveid.get(0).getStatus());
		
		verify(repository).findByStatus(ReservationStatus.CONFIRMADA);
		
	}
	
	@Test
	@DisplayName("Should return an empty list when there aren't any objects with status")
	void findByStatusEmptyCase() {
		List<ReservationSummaryDTO> listReceveid = service.findByStatus("STATUS_INEXISTENTE");
		
		assertNotNull(listReceveid);
		assertTrue(listReceveid.isEmpty());
		
		verify(repository, never()).findByStatus(any());
	}
	
	@Test
	@DisplayName("Should throw ResourceNotFoundException when id doesn't exist")
	void findByIdNotSuccessfulCase() {
		when(repository.findById(99l)).thenReturn(Optional.empty());
		
		assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(99l);
		});
		
		verify(repository).findById(99l);
	}	
	@Test
	@DisplayName("Should calculate total value correctly and save reservation")
	void insertSuccessCase() {
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		ReflectionTestUtils.setField(r1, "id", 1L);
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		
		ReservationRequestDTO dto = new ReservationRequestDTO();
	    dto.setCheckInDate(LocalDate.of(2025, 11, 25));
	    dto.setCheckOutDate(LocalDate.of(2025, 11, 28));
	    dto.setGuestId(1L);
	    dto.setRoomId(1L);
	    
	    when(guestRepository.findById(1L)).thenReturn(Optional.of(g1));
	    when(roomRepository.findById(1L)).thenReturn(Optional.of(r1));
	    
	    when(repository.save(any(Reservation.class))).thenAnswer(invocation ->{
	    	Reservation rv = invocation.getArgument(0);
	    	rv.setId(1l);
	    	return rv;
	    });
	    
	    ReservationResponseDTO rvExpected = service.insert(dto);
	    
	    assertNotNull(rvExpected);
	    
	    ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
	    verify(repository).save(reservationCaptor.capture());
	    
	    Reservation capturedReservation = reservationCaptor.getValue();
	    
	    assertEquals(new BigDecimal("570.00"), capturedReservation.getTotalValue());
	    assertEquals(ReservationStatus.PENDENTE, capturedReservation.getStatus());
	}
}
