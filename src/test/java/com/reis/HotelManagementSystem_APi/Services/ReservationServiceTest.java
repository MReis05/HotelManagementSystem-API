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

import com.reis.HotelManagementSystem_APi.dto.PaymentRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationSummaryDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Payment;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.Stay;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentType;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.repositories.PaymentRepository;
import com.reis.HotelManagementSystem_APi.repositories.ReservationRepository;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;
import com.reis.HotelManagementSystem_APi.services.ReservationService;
import com.reis.HotelManagementSystem_APi.services.exceptions.CheckOutException;
import com.reis.HotelManagementSystem_APi.services.exceptions.InvalidActionException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;
import com.reis.HotelManagementSystem_APi.services.exceptions.RoomUnavailableException;

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
		Reservation rv = createStandardReservation();
		
		List<Reservation> listExpected = List.of(rv);
		when(repository.findByStatus(ReservationStatus.CONFIRMADA)).thenReturn(listExpected);
		
		List<ReservationSummaryDTO> listReceived = service.findByStatus(ReservationStatus.CONFIRMADA.name());
		
		assertNotNull(listReceived);
		
		assertEquals(1, listReceived.size());
		
		assertEquals(rv.getId(), listReceived.get(0).getId());
		assertEquals(rv.getTotalValue(), listReceived.get(0).getTotalValue());
		assertEquals(rv.getStatus(), listReceived.get(0).getStatus());
		
		verify(repository).findByStatus(ReservationStatus.CONFIRMADA);
		
	}
	
	@Test
	@DisplayName("Should return an empty list when there aren't any objects with status")
	void findByStatusEmptyCase() {
		List<ReservationSummaryDTO> listReceived = service.findByStatus("STATUS_INEXISTENTE");
		
		assertNotNull(listReceived);
		assertTrue(listReceived.isEmpty());
		
		verify(repository, never()).findByStatus(any());
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
	    	rv.setId(1L);
	    	return rv;
	    });
	    
	    ReservationResponseDTO rvReceived = service.insert(dto);
	    
	    assertNotNull(rvReceived);
	    
	    assertEquals(new BigDecimal("570.00"), rvReceived.getTotalValue());
	    assertEquals(ReservationStatus.PENDENTE, rvReceived.getStatus());
	}
	
	@Test
	@DisplayName("Should throw a RoomUnavailableException when room is under maintenance")
	void insertRoomUnavailableCase() {
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.MANUTENCAO, RoomType.SOLTEIRO);
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
	    
	   RoomUnavailableException exception = assertThrows(RoomUnavailableException.class, () ->{
	    	service.insert(dto);
	    });
	    
	   assertNotNull(exception.getMessage());
	   assertEquals(RoomUnavailableException.class, exception.getClass());
	    
	   verify(repository, never()).save(any());
	}
	
	@Test
	@DisplayName("Should create reservation when dates DO NOT overlap with existing bookings")
	void insertCheckAvailabilitySuccessCase() {
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		ReflectionTestUtils.setField(r1, "id", 1L);
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		Reservation rv1 = new Reservation(LocalDate.of(2025, 11, 29), LocalDate.of(2025, 11, 30), ReservationStatus.CONFIRMADA);
		r1.getReservations().add(rv1);
		
		ReservationRequestDTO dto = new ReservationRequestDTO();
	    dto.setCheckInDate(LocalDate.of(2025, 11, 25));
	    dto.setCheckOutDate(LocalDate.of(2025, 11, 28));
	    dto.setGuestId(1L);
	    dto.setRoomId(1L);
	    
	    when(guestRepository.findById(1L)).thenReturn(Optional.of(g1));
	    when(roomRepository.findById(1L)).thenReturn(Optional.of(r1));
	    
	    when(repository.save(any(Reservation.class))).thenAnswer(invocation ->{
	    	Reservation rv = invocation.getArgument(0);
	    	rv.setId(1L);
	    	return rv;
	    });
	    
	    ReservationResponseDTO rvReceived = service.insert(dto);
	    
	    assertNotNull(rvReceived);
	    
	    assertEquals(new BigDecimal("570.00"), rvReceived.getTotalValue());
	    assertEquals(ReservationStatus.PENDENTE, rvReceived.getStatus());
	}
	
	@Test
	@DisplayName("Should throw RoomUnavailableException when requested dates overlap with an existing reservation")
	void insertCheckAvailabilityBadCase () {
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		ReflectionTestUtils.setField(r1, "id", 1L);
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		Reservation rv1 = new Reservation(LocalDate.of(2025, 11, 26), LocalDate.of(2025, 11, 30), ReservationStatus.CONFIRMADA);
		rv1.setId(1L);
		r1.getReservations().add(rv1);
		
		ReservationRequestDTO dto = new ReservationRequestDTO();
	    dto.setCheckInDate(LocalDate.of(2025, 11, 25));
	    dto.setCheckOutDate(LocalDate.of(2025, 11, 28));
	    dto.setGuestId(1L);
	    dto.setRoomId(1L);
	    
	    when(guestRepository.findById(1L)).thenReturn(Optional.of(g1));
	    when(roomRepository.findById(1L)).thenReturn(Optional.of(r1));
	    
	    RoomUnavailableException exception = assertThrows(RoomUnavailableException.class, () ->{
	    	service.insert(dto);
	    });
	    
	    assertNotNull(exception.getMessage());
	    assertEquals(RoomUnavailableException.class, exception.getClass());
	    
	    verify(repository, never()).save(any());
	    		
	}
	
	@Test
	@DisplayName("Should update the Reservation and calculate the new total value correctly and save reservation")
	void updateSuccessCase() {
		Reservation oldReservation = createStandardReservation();
		ReservationRequestDTO dto = new ReservationRequestDTO();
	    dto.setCheckInDate(LocalDate.of(2025, 11, 29));
	    dto.setCheckOutDate(LocalDate.of(2025, 11, 30));
	    dto.setGuestId(1L);
	    dto.setRoomId(1L);
	    
	    when(repository.findById(1L)).thenReturn(Optional.of(oldReservation));
	    when(roomRepository.findById(1L)).thenReturn(Optional.of(oldReservation.getRoom()));
	    when(guestRepository.findById(1L)).thenReturn(Optional.of(oldReservation.getGuest()));
	    
	    when(repository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
	    
	    ReservationResponseDTO newReservation = service.updateReservation(1L, dto);
	    
	    assertNotNull(newReservation);
	    
	    assertEquals(new BigDecimal("190.00"), newReservation.getTotalValue());
	    assertEquals(ReservationStatus.PENDENTE, newReservation.getStatus());
	    assertEquals(1L, newReservation.getRoomSummaryDTO().getId());
	    assertEquals(1L, newReservation.getGuestSummaryDTO().getId());
	}
	
	@Test
	@DisplayName("Should throw an InvalidActionException when Reservation Status is canceled or concluded")
	void updateInvalidActionCase() {
		Reservation oldReservation = new Reservation(LocalDate.of(2025, 11, 25), LocalDate.of(2025, 11, 28), ReservationStatus.CANCELADA);
		ReflectionTestUtils.setField(oldReservation, "id", 1L);
		ReservationRequestDTO dto = new ReservationRequestDTO();
	    dto.setCheckInDate(LocalDate.of(2025, 11, 29));
	    dto.setCheckOutDate(LocalDate.of(2025, 11, 30));
	    dto.setGuestId(1L);
	    dto.setRoomId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(oldReservation));
		
		InvalidActionException exception = assertThrows(InvalidActionException.class, () ->{
			service.updateReservation(1L, dto);
		});
		
		assertNotNull(exception.getMessage());
		
		verify(repository, never()).save(any());
	}
	
	@Test
	@DisplayName("Should confirm reservation when payment is equal or greater than 50%")
	void confirmReservationSuccessCase() {
		Reservation rv = createStandardReservation();
		PaymentRequestDTO dto = new PaymentRequestDTO();
		dto.setAmount(new BigDecimal("300.00"));
		dto.setReservationId(1L);
		dto.setType(PaymentType.PIX);
		
		when(repository.findById(1L)).thenReturn(Optional.of(rv));
		
		when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation ->{
			Payment p = invocation.getArgument(0);
			ReflectionTestUtils.setField(p, "id", 1L);
			p.setStatus(PaymentStatus.APROVADO);
			return p;
		});
		
		 when(repository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
		ReservationResponseDTO rvReceived = service.confirmReservation(1l, dto);
		
		assertNotNull(rvReceived);
		
		assertEquals(ReservationStatus.CONFIRMADA, rvReceived.getStatus());
	}
	
	@Test
	@DisplayName("Should maintain reservation status as pending when payment is lower than 50%")
	void confirmReservationBadCase() {
		Reservation rv = createStandardReservation();
		PaymentRequestDTO dto = new PaymentRequestDTO();
		dto.setAmount(new BigDecimal("100.00"));
		dto.setReservationId(1L);
		dto.setType(PaymentType.PIX);
		
		when(repository.findById(1L)).thenReturn(Optional.of(rv));
		
		when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation ->{
			Payment p = invocation.getArgument(0);
			ReflectionTestUtils.setField(p, "id", 1L);
			p.setStatus(PaymentStatus.APROVADO);
			return p;
		});
		
		 when(repository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		ReservationResponseDTO rvReceived = service.confirmReservation(1l, dto);
		
		assertNotNull(rvReceived);
		
		assertEquals(ReservationStatus.PENDENTE, rvReceived.getStatus());
	}
	
	@Test
	@DisplayName("Should perform checkout when balance is zero")
	void performCheckOutSuccessCase() {
		Reservation rv = new Reservation(LocalDate.now().minusDays(3), LocalDate.now(), ReservationStatus.EM_CURSO);
	    rv.setId(1L);
	    rv.setTotalValue(new BigDecimal("300.00"));
	    rv.getPayments().clear();
	    
	    Room r1 = new Room(1, new BigDecimal("100.00"), "Quarto com Ventilador", RoomStatus.OCUPADO, RoomType.SOLTEIRO);
	    rv.setRoom(r1);

	    Payment pay = new Payment(Instant.now(), PaymentStatus.APROVADO, PaymentType.CARTAO_DE_CREDITO, new BigDecimal("300.00"), rv);
	    rv.getPayments().add(pay);

	    Stay stay = new Stay();
	    stay.setReservation(rv);
	    stay.setCheckOutDate(LocalDateTime.now());

	    when(repository.findById(1L)).thenReturn(Optional.of(rv));

	    service.performCheckOut(stay);

	    assertEquals(ReservationStatus.CONCLUIDA, rv.getStatus());
	    assertEquals(RoomStatus.LIMPEZA, rv.getRoom().getStatus());
	}
	
	@Test
	@DisplayName("Should throw a CheckOutException when there are pending values to be paid")
	void performCheckOutBadCase() {
		Reservation rv = new Reservation(LocalDate.now().minusDays(3), LocalDate.now(), ReservationStatus.EM_CURSO);
	    rv.setId(1L);
	    rv.setTotalValue(new BigDecimal("300.00"));
	    rv.getPayments().clear();
	    
	    Room r1 = new Room(1, new BigDecimal("100.00"), "Quarto com Ventilador", RoomStatus.OCUPADO, RoomType.SOLTEIRO);
	    rv.setRoom(r1);

	    Payment pay = new Payment(Instant.now(), PaymentStatus.APROVADO, PaymentType.CARTAO_DE_CREDITO, new BigDecimal("200.00"), rv);
	    rv.getPayments().add(pay);

	    Stay stay = new Stay();
	    stay.setReservation(rv);
	    stay.setCheckOutDate(LocalDateTime.now());

	    when(repository.findById(1L)).thenReturn(Optional.of(rv));

	    CheckOutException exception = assertThrows(CheckOutException.class, () -> {
	    	service.performCheckOut(stay);
	    });
	    
	    assertNotNull(exception.getMessage());
	    assertEquals(CheckOutException.class, exception.getClass());
	}
	
	@Test
	@DisplayName("Should cancel reservation and update status to CANCELADA")
	void cancelReservationSuccessCase() {
		Reservation rv = createStandardReservation();
		
		when(repository.findById(1L)).thenReturn(Optional.of(rv));
		
		when(repository.save(any(Reservation.class))).thenAnswer(invocation  -> invocation.getArgument(0));
		ReservationResponseDTO rvReceived = service.cancelReservation(1L);
		
		assertNotNull(rvReceived);
		
		assertEquals(ReservationStatus.CANCELADA, rvReceived.getStatus());
		
		verify(repository).save(any(Reservation.class));
	}
	
	@Test
	@DisplayName("Should update reservation status to EM_CURSO and room status to OCUPADO")
	void checkInStaySuccessCase() {
		Reservation rv = new Reservation(LocalDate.of(2025, 11, 25), LocalDate.of(2025, 11, 28), ReservationStatus.CONFIRMADA);
		rv.setId(1L);
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		ReflectionTestUtils.setField(r1, "id", 1L);
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		rv.setRoom(r1);
		rv.setGuest(g1);
		
		when(repository.findById(1L)).thenReturn(Optional.of(rv));

		Reservation rvReceived = service.checkInStay(1L);
		
		assertNotNull(rvReceived);
		assertEquals(ReservationStatus.EM_CURSO, rvReceived.getStatus());
		assertEquals(RoomStatus.OCUPADO, rvReceived.getRoom().getStatus());
	}
	
	private Reservation createStandardReservation() {
		Reservation rv = new Reservation(LocalDate.of(2025, 11, 25), LocalDate.of(2025, 11, 28), ReservationStatus.PENDENTE);
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		ReflectionTestUtils.setField(r1, "id", 1L);
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		ReflectionTestUtils.setField(g1, "id", 1L);
		rv.setId(1L);
		rv.setTotalValue(new BigDecimal("570.00"));
		rv.setRoom(r1);
		rv.setGuest(g1);
		return rv;
	}
}
