package com.reis.HotelManagementSystem_APi.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.HotelManagementSystem_APi.dto.ReservationRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationResponseDTO;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.repositories.ReservationRepository;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;
import com.reis.HotelManagementSystem_APi.services.exceptions.InvalidActionException;
import com.reis.HotelManagementSystem_APi.services.exceptions.InvalidDurationReservationException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;
import com.reis.HotelManagementSystem_APi.services.exceptions.RoomUnavailableException;

@Service
public class ReservationService {

	@Autowired
	private ReservationRepository repository;
	
	@Autowired
	private GuestRepository guestRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	public List<ReservationResponseDTO> findAll(){
		List<Reservation> list = repository.findAll();
		List<ReservationResponseDTO> dto = list.stream().map(ReservationResponseDTO::new).collect(Collectors.toList());
		return dto;
	}
	
	public ReservationResponseDTO findById(Long id) {
		Reservation obj = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
		return new ReservationResponseDTO(obj);
	}
	
	public List<ReservationResponseDTO> findByStatus(String status) {
		ReservationStatus reservationStatus;
		try {
			reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
		}
		catch(IllegalArgumentException e) {
			return Collections.emptyList();
		}
		
		List<Reservation> list = repository.findByStatus(reservationStatus);
		
		List<ReservationResponseDTO> dto = list.stream().map(ReservationResponseDTO::new).collect(Collectors.toList());
		
		return dto;
	}
	
	public ReservationResponseDTO insert (ReservationRequestDTO dto) {
		Guest guest = guestRepository.findById(dto.getGuestId()).orElseThrow(()-> new ResourceNotFoundException(dto.getGuestId()));
		Room room = roomRepository.findById(dto.getRoomId()).orElseThrow(()-> new ResourceNotFoundException(dto.getRoomId()));
		
		if (room.getStatus() == RoomStatus.MANUTENCAO) {
			throw new RoomUnavailableException(room.getNumber());
		}
		double totalValue = calculateTotalStayCost(dto, room.getPricePerNight());
		
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
	
	public ReservationResponseDTO updateStatus(Long id, String action, ReservationRequestDTO dto) {
		if (action == null || action.isEmpty()) {
			throw new InvalidActionException();
		}
		
		action = action.toUpperCase();
		switch(action) {
		case "CANCELAR":{
			ReservationResponseDTO resp = cancelReservation(id);
			return resp;
		}
		case "ATUALIZAR":{
			ReservationResponseDTO resp = updateReservation(id, dto);
			return resp;
		}
		default:{
			throw new InvalidActionException();
		}
		}
		
	}
	
	public ReservationResponseDTO cancelReservation (Long id) {
		Reservation obj = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
		obj.setStatus(ReservationStatus.valueOf("CANCELADA"));
		obj = repository.save(obj);
		return new ReservationResponseDTO(obj);
	}
	
	public ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO dto) {
		Reservation obj = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
		
		if(obj.getStatus() == ReservationStatus.CANCELADA || obj.getStatus() == ReservationStatus.CONCLUIDA) {
			throw new InvalidActionException();
		}
		
		updateData(obj, dto);
		checkAvailability(obj.getRoom(), obj.getCheckInDate(), obj.getCheckOutDate(), id);
		obj.setTotalValue(calculateTotalStayCost(dto, obj.getRoom().getPricePerNight()));
		repository.save(obj);
		return new ReservationResponseDTO(obj);
	}
	
	public void updateData (Reservation obj, ReservationRequestDTO dto) {
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
			obj.setRoom(roomRepository.findById(dto.getRoomId()).orElseThrow(()-> new ResourceNotFoundException(dto.getRoomId())));
			obj.setStatus(ReservationStatus.PENDENTE);
		}
	}
	
	private void checkAvailability(Room room, LocalDate newCheckIn, LocalDate newCheckOut, Long id) {
		for (Reservation reservation: room.getReservation()) {
			if(reservation.getStatus() == ReservationStatus.CANCELADA) {
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

	private double calculateTotalStayCost(ReservationRequestDTO dto, double pricePerNight) {
		long days = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
		if (days <= 0) {
			throw new InvalidDurationReservationException();
		}
		return pricePerNight * days;
	}
}
