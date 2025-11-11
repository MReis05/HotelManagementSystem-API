package com.reis.HotelManagementSystem_APi.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.reis.HotelManagementSystem_APi.services.exceptions.DatabaseException;
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
	
	public ReservationResponseDTO insert (ReservationRequestDTO dto) {
		Guest guest = guestRepository.findById(dto.getGuestId()).orElseThrow(()-> new ResourceNotFoundException(dto.getGuestId()));
		Room room = roomRepository.findById(dto.getRoomId()).orElseThrow(()-> new ResourceNotFoundException(dto.getRoomId()));
		
		if (room.getStatus() == RoomStatus.MANUTENCAO) {
			throw new RoomUnavailableException(room.getNumber());
		}
		double totalValue = calculateDailyCharges(dto, room.getPricePerNight());
		
		Reservation obj = new Reservation();
		obj.setCheckInDate(dto.getCheckInDate());
		obj.setCheckOutDate(dto.getCheckOutDate());
		obj.setGuest(guest);
		obj.setRoom(room);
		obj.setStatus(ReservationStatus.PENDENTE);
		obj.setTotalValue(totalValue);
		
		checkAvailability(room, obj.getCheckInDate(), obj.getCheckOutDate());

		obj = repository.save(obj);
		
		return new ReservationResponseDTO(obj);
	}
	
	public void delete (Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException(id);
		}
		try {
			repository.deleteById(id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	private void checkAvailability(Room room, LocalDate newCheckIn, LocalDate newCheckOut) {
		for (Reservation reservation: room.getReservation()) {
			if(reservation.getStatus() == ReservationStatus.CANCELADA) {
				continue;
			}
			
			boolean overlaps = (newCheckIn.isBefore(reservation.getCheckOutDate())) &&
					            (newCheckOut.isAfter(reservation.getCheckInDate()));
			
			if(overlaps) {
				throw new RoomUnavailableException(room.getNumber());
			}
		}
	}

	private double calculateDailyCharges(ReservationRequestDTO dto, double pricePerNight) {
		long days = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
		if (days <= 0) {
			throw new InvalidDurationReservationException();
		}
		return pricePerNight * days;
	}
}
