package com.reis.HotelManagementSystem_APi.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.reis.HotelManagementSystem_APi.dto.IncidentalRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.IncidentalResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.PaymentRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.PaymentResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.StayRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.StayResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.StaySummaryDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Incidental;
import com.reis.HotelManagementSystem_APi.entities.Payment;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.Stay;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentType;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.IncidentalRepository;
import com.reis.HotelManagementSystem_APi.repositories.StayRepository;
import com.reis.HotelManagementSystem_APi.services.ReservationService;
import com.reis.HotelManagementSystem_APi.services.StayService;
import com.reis.HotelManagementSystem_APi.services.exceptions.CheckInDateException;
import com.reis.HotelManagementSystem_APi.services.exceptions.InvalidActionException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class StayServiceTest {

	@Mock
	private StayRepository repository;
	
	@Mock
	private IncidentalRepository incidentalRepository;
	
	@Mock
	private ReservationService reservationService;
	
	@InjectMocks
	private StayService service;
	
	@Test
	@DisplayName("Should return a List<StaySummaryDTO> when find objects")
	void findAllSuccessCase() {
		Stay s1 = createStandardStay();
		List<Stay> listExpected = List.of(s1);
		when(repository.findAll()).thenReturn(listExpected);
		
		List<StaySummaryDTO> listReceived = service.findAll();
		
		assertNotNull(listReceived);
		assertEquals(1L, listReceived.get(0).getId());
		assertEquals(1L, listReceived.get(0).getGuestSummaryDTO().getId());
		
		verify(repository).findAll();
	}
	
	@Test
	@DisplayName("Should return a StayResponseDTO when find object")
	void findByIdSuccessCase() {
		Stay s1 = createStandardStay();
		
		when(repository.findById(1L)).thenReturn(Optional.of(s1));
		
		StayResponseDTO s1Received = service.findById(1L);
		
		assertNotNull(s1Received);
		assertEquals(1L, s1Received.getId());
		assertEquals(1L, s1Received.getGuestSummaryDTO().getId());
		assertEquals(1L, s1Received.getReservationId());
		assertEquals(1, s1Received.getIncidentalsList().size());
		
		verify(repository).findById(1L);
	}
	
	@Test
	@DisplayName("Should throw ResourceNotFoundException when id doesn't exist")
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
	@DisplayName("Should create Stay when dates match reservation check-in date")
	void checkInSuccessCase() {
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		ReflectionTestUtils.setField(r1, "id", 1L);
		Reservation rv1 = new Reservation(LocalDate.of(2026, 01, 01), LocalDate.of(2026, 01, 5), ReservationStatus.CONFIRMADA);
		rv1.setId(1L);
		rv1.setRoom(r1);
		rv1.setGuest(g1);
		rv1.setTotalValue(new BigDecimal("760.00"));
		when(reservationService.checkInStay(1L)).thenReturn(rv1);
		
		StayRequestDTO dto = new StayRequestDTO();
		dto.setCheckInDate(LocalDateTime.of(2026,01, 01, 14, 00));
		dto.setReservationId(1L);
		
		when(repository.save((any(Stay.class)))).thenAnswer(invocation -> {
			Stay s = invocation.getArgument(0);
			ReflectionTestUtils.setField(s, "id", 1L);
			return s;
		});
		
		StayResponseDTO stayReceived = service.checkIn(dto);
		
		assertNotNull(stayReceived);
		assertEquals(1L, stayReceived.getId());
		assertEquals(LocalDateTime.of(2026, 01, 01, 14, 00), stayReceived.getCheckInDate());
		assertEquals(new BigDecimal("760.00"), stayReceived.getTotalValue());
		
		verify(repository).save(any(Stay.class));
		verify(reservationService).checkInStay(1L);
	}
	
	@Test
	@DisplayName("Should throw CheckInDateException when dates do not match")
	void checkInDateMismacthCase() {
		Reservation rv1 = new Reservation(LocalDate.of(2026, 01, 01), LocalDate.of(2026, 01, 5), ReservationStatus.CONFIRMADA);
		rv1.setId(1L);
		when(reservationService.checkInStay(1L)).thenReturn(rv1);
		
		StayRequestDTO dto = new StayRequestDTO();
		dto.setCheckInDate(LocalDateTime.of(2026,01, 02, 14, 00));
		dto.setReservationId(1L);
		
		CheckInDateException exception = assertThrows(CheckInDateException.class, () -> {
			service.checkIn(dto);
		});
		
		assertNotNull(exception);
		assertEquals(CheckInDateException.class, exception.getClass());
		
		verify(repository, never()).save(any());
	}
	
	@Test
	@DisplayName("Should close stay and delegate calculation to ReservationService")
	void checkOutSuccessCase() {
		Stay stay = createStandardStay();
		
		when(repository.findById(1L)).thenReturn(Optional.of(stay));
		
		when(repository.save(any(Stay.class))).thenAnswer(i -> i.getArgument(0));

	    StayResponseDTO stayReceivved = service.checkOut(1L);

	    assertNotNull(stayReceivved);
	    assertNotNull(stayReceivved.getCheckOutDate());
	    
	    verify(reservationService).performCheckOut(stay);
	    verify(repository).save(stay);
	}
	
	@Test
	@DisplayName("Should throw an InvalidActionException when Stay is already finished")
	void checkOutAlreadyFinishedCase() {
		Stay stay = createStandardStay();
		stay.setCheckOutDate(LocalDateTime.now());
		
		when(repository.findById(1L)).thenReturn(Optional.of(stay));
		
		InvalidActionException exception = assertThrows(InvalidActionException.class, () ->{
			service.checkOut(1L);
		});
		
		assertNotNull(exception);
		assertEquals(InvalidActionException.class, exception.getClass());
		
		verify(repository, never()).save(any());
	}
	
	@Test
	@DisplayName("Should add incidental when stay is active")
	void addIncidentalSuccessCase() {
		Stay stay = createStandardStay();
		
		when(repository.findById(1L)).thenReturn(Optional.of(stay));
		
		IncidentalRequestDTO dto = new IncidentalRequestDTO();
		dto.setName("Agua Mineral");
		dto.setPrice(new BigDecimal("5.00"));
		dto.setQuantity(2);
		
		when(incidentalRepository.save(any())).thenAnswer(invocation ->{
			Incidental i = invocation.getArgument(0);
			ReflectionTestUtils.setField(i, "id", 1L);
			return i;
		});
		
		IncidentalResponseDTO incidentalReceived = service.addIncidental(1L, dto);
		
		assertNotNull(incidentalReceived);
		assertNotNull(incidentalReceived.getMoment());
		assertEquals(1L, incidentalReceived.getId());
		assertEquals("Agua Mineral", incidentalReceived.getName());
		assertEquals(new BigDecimal("5.00"), incidentalReceived.getPrice());
		assertEquals(new BigDecimal("10.00"), incidentalReceived.getTotal());
		assertEquals(2, incidentalReceived.getQuantity());
		assertEquals(2, stay.getIncidentals().size());

		
		verify(incidentalRepository).save(any());
	}
	
	@Test
	@DisplayName("Should throw an InvalidActionException when Stay is already finished")
	void addInicidentalStayFinishedCase() {
		Stay stay = createStandardStay();
		stay.setCheckOutDate(LocalDateTime.now());
		
		when(repository.findById(1L)).thenReturn(Optional.of(stay));
		
		IncidentalRequestDTO dto = new IncidentalRequestDTO();
		
		InvalidActionException exception = assertThrows(InvalidActionException.class, () ->{
			service.addIncidental(1L, dto);
		});
		
		assertEquals(InvalidActionException.class, exception.getClass());
		
		verify(incidentalRepository, never()).save(any());
	}
	
	@Test
	@DisplayName("Should delegate payment processing to ReservationService")
	void makePaymentSuccessCase() {
	    Stay stay = createStandardStay();
	    PaymentRequestDTO dto = new PaymentRequestDTO(); 
	   
	    Payment pay = new Payment(); 
	    pay.setAmount(new BigDecimal("100.00"));
	    pay.setMoment(Instant.now());
	    pay.setReservation(stay.getReservation());
	    pay.setStatus(PaymentStatus.APROVADO);
	    pay.setType(PaymentType.PIX);
	    
	    when(repository.findById(1L)).thenReturn(Optional.of(stay));
	    when(reservationService.processPayment(dto, stay.getReservation())).thenReturn(pay);

	    PaymentResponseDTO paymentReceied = service.makePayment(1L, dto);

	    assertNotNull(paymentReceied);
	    assertEquals(new BigDecimal("100.00"), paymentReceied.getAmount());
	    
	    verify(reservationService).processPayment(dto, stay.getReservation());
	}
	
	private Stay createStandardStay() {
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.OCUPADO, RoomType.SOLTEIRO);
		ReflectionTestUtils.setField(r1, "id", 1L);
		Reservation rv1 = new Reservation(LocalDate.of(2026, 01, 01), LocalDate.of(2026, 01, 05), ReservationStatus.EM_CURSO);
		rv1.setId(1L);
		rv1.setGuest(g1);
		rv1.setRoom(r1);
		Stay s = new Stay();
		ReflectionTestUtils.setField(s, "id", 1L);
		s.setCheckInDate(LocalDateTime.of(2026, 01, 01, 14, 00));
		s.setReservation(rv1);
		Incidental i = new Incidental("Coca-Cola", 2, new BigDecimal("5.00"), LocalDateTime.of(2026, 01, 02, 20, 00), s);
		ReflectionTestUtils.setField(i, "id", 1L);
		s.getIncidentals().add(i);
		return s;
	}
}
