package com.reis.HotelManagementSystem_APi.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.HotelManagementSystem_APi.dto.PaymentRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationSummaryDTO;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Payment;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.Stay;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.repositories.PaymentRepository;
import com.reis.HotelManagementSystem_APi.repositories.ReservationRepository;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;
import com.reis.HotelManagementSystem_APi.services.exceptions.CheckOutException;
import com.reis.HotelManagementSystem_APi.services.exceptions.InvalidActionException;
import com.reis.HotelManagementSystem_APi.services.exceptions.InvalidDurationReservationException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;
import com.reis.HotelManagementSystem_APi.services.exceptions.RoomUnavailableException;

import jakarta.transaction.Transactional;

@Service
public class ReservationService {

	@Autowired
	private ReservationRepository repository;
	
	@Autowired
	private GuestRepository guestRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	public List<ReservationSummaryDTO> findAll(){
		List<Reservation> list = repository.findAll();
		List<ReservationSummaryDTO> dto = list.stream().map(ReservationSummaryDTO::new).collect(Collectors.toList());
		return dto;
	}
	
	public ReservationResponseDTO findById(Long id) {
		Reservation obj = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
		return new ReservationResponseDTO(obj);
	}
	
	public List<ReservationSummaryDTO> findByStatus(String status) {
		ReservationStatus reservationStatus;
		try {
			reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
		}
		catch(IllegalArgumentException e) {
			return Collections.emptyList();
		}
		
		List<Reservation> list = repository.findByStatus(reservationStatus);
		
		List<ReservationSummaryDTO> dto = list.stream().map(ReservationSummaryDTO::new).collect(Collectors.toList());
		
		return dto;
	}
	
	@Transactional
	public ReservationResponseDTO insert (ReservationRequestDTO dto) {
		Guest guest = guestRepository.findById(dto.getGuestId()).orElseThrow(()-> new ResourceNotFoundException(dto.getGuestId()));
		Room room = roomRepository.findById(dto.getRoomId()).orElseThrow(()-> new ResourceNotFoundException(dto.getRoomId()));
		
		if (room.getStatus() == RoomStatus.MANUTENCAO) {
			throw new RoomUnavailableException(room.getNumber());
		}
		BigDecimal totalValue = calculateTotalStayCost(dto, room.getPricePerNight());
		
		Reservation obj = new Reservation();
		obj.setCheckInDate(dto.getCheckInDate());
		obj.setCheckOutDate(dto.getCheckOutDate());
		obj.setGuest(guest);
		obj.setRoom(room);
		obj.setStatus(ReservationStatus.PENDENTE);
		obj.setTotalValue(totalValue);
		
		checkAvailability(room, obj.getCheckInDate(), obj.getCheckOutDate(), null);

		obj = repository.save(obj);
		
		return new ReservationResponseDTO(obj);
	}
	
	@Transactional
	public ReservationResponseDTO cancelReservation (Long id) {
		Reservation obj = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
		obj.setStatus(ReservationStatus.valueOf("CANCELADA"));
		obj = repository.save(obj);
		return new ReservationResponseDTO(obj);
	}
	
	@Transactional
	public ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO dto) {
		Reservation obj = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
		
		if(obj.getStatus() == ReservationStatus.CANCELADA || obj.getStatus() == ReservationStatus.CONCLUIDA) {
			throw new InvalidActionException("Não é possivel atualizar dados de uma reserva cancelada ou concluída");
		}
		
		updateData(obj, dto);
		checkAvailability(obj.getRoom(), obj.getCheckInDate(), obj.getCheckOutDate(), id);
		obj.setTotalValue(calculateTotalStayCost(dto, obj.getRoom().getPricePerNight()));
		repository.save(obj);
		return new ReservationResponseDTO(obj);
	}
	
	@Transactional
	public ReservationResponseDTO confirmReservation(Long id, PaymentRequestDTO dto) {
		Reservation obj = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
		processPayment(dto, obj);
		BigDecimal totalPaid = obj.getPayments().stream().filter(p -> p.getStatus() == PaymentStatus.APROVADO)
				.map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal percent = new BigDecimal("0.50");
		BigDecimal minimumValue = obj.getTotalValue().multiply(percent);
		if(totalPaid.compareTo(minimumValue) >= 0) {
			if (obj.getStatus() == ReservationStatus.PENDENTE) {
				obj.setStatus(ReservationStatus.CONFIRMADA);
			}
		}
		
		obj = repository.save(obj);

		return new ReservationResponseDTO(obj);
	}
	
	@Transactional
	public Reservation checkInStay(Long id) {
		Reservation obj = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
		
		obj.setStatus(ReservationStatus.EM_CURSO);
		
		if(obj.getRoom() != null) {
			obj.getRoom().setStatus(RoomStatus.OCUPADO);
		}
		
		return obj;
	}
	
	@Transactional
	public void performCheckOut(Stay stay) {
		Reservation obj = repository.findById(stay.getReservation().getId()).orElseThrow(()-> new ResourceNotFoundException(stay.getReservation().getId()));
		
		obj.setCheckOutDate(stay.getCheckOutDate().toLocalDate());
		
		BigDecimal newTotalStayCost = calculateInternalTotalStayCost(obj.getCheckInDate(), obj.getCheckOutDate(), obj.getRoom().getPricePerNight());
		obj.setTotalValue(newTotalStayCost);
		
		BigDecimal totalPaid = obj.getPayments().stream().filter(p -> p.getStatus() == PaymentStatus.APROVADO)
				.map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal finalBill = obj.getTotalValue().add(stay.getIncidentalsTotalValue());
		
		if(finalBill.compareTo(totalPaid) == 0) {
			obj.setStatus(ReservationStatus.CONCLUIDA);
			if(obj.getRoom() != null) {
				obj.getRoom().setStatus(RoomStatus.LIMPEZA);
			}
		}
		else {
			throw new CheckOutException("Não é possível realizar o Check-Out com valores ainda pendentes. Valor da reserva: " + obj.getTotalValue()
										+ " Total pago: " + totalPaid);
		}
	}
	
	private void updateData (Reservation obj, ReservationRequestDTO dto) {
		if(dto.getCheckInDate() != null) {
			obj.setCheckInDate(dto.getCheckInDate());
			obj.setStatus(ReservationStatus.PENDENTE);

		}
		if(dto.getCheckOutDate() != null) {
			obj.setCheckOutDate(dto.getCheckOutDate());
			obj.setStatus(ReservationStatus.PENDENTE);

		}
		if(dto.getGuestId() != null) {
			obj.setGuest(guestRepository.findById(dto.getGuestId()).orElseThrow(()-> new ResourceNotFoundException(dto.getGuestId())));
		}
		if(dto.getRoomId() != null) {
			Room room = roomRepository.findById(dto.getRoomId()).orElseThrow(()-> new ResourceNotFoundException(dto.getRoomId()));
			
			if (room.getStatus() == RoomStatus.MANUTENCAO) {
				throw new RoomUnavailableException(room.getNumber());
			}
			
			obj.setRoom(room);
			obj.setStatus(ReservationStatus.PENDENTE);
		}
	}
	
	private void checkAvailability(Room room, LocalDate newCheckIn, LocalDate newCheckOut, Long id) {
		for (Reservation reservation: room.getReservations()) {
			if(reservation.getStatus() == ReservationStatus.CANCELADA || reservation.getStatus() == ReservationStatus.CONCLUIDA) {
				continue;
			}
			if(reservation.getId() == id) {
				continue;
			}
			
			boolean overlaps = (newCheckIn.isBefore(reservation.getCheckOutDate())) &&
					            (newCheckOut.isAfter(reservation.getCheckInDate()));
			
			if(overlaps) {
				throw new RoomUnavailableException(room.getNumber());
			}
		}
	}

	private BigDecimal calculateTotalStayCost(ReservationRequestDTO dto, BigDecimal pricePerNight) {
		long days = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
		if (days <= 0) {
			throw new InvalidDurationReservationException();
		}
		return pricePerNight.multiply(BigDecimal.valueOf(days));
	}
	
	private BigDecimal calculateInternalTotalStayCost(LocalDate checkInDate, LocalDate checkOutDate, BigDecimal pricePerNight) {
		long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
		if(days <= 0) days = 1;
		return pricePerNight.multiply(BigDecimal.valueOf(days));
	}
	
	public Payment processPayment(PaymentRequestDTO dto, Reservation reservation) {
		Payment payment = new Payment();
		payment.setAmount(dto.getAmount().setScale(2, RoundingMode.HALF_EVEN));
		payment.setMoment(Instant.now());
		payment.setStatus(PaymentStatus.APROVADO);
		payment.setType(dto.getType());
		payment.setReservation(reservation);
		reservation.getPayments().add(payment);
		payment = paymentRepository.save(payment);
		return payment;
	}
}
