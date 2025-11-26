package com.reis.HotelManagementSystem_APi.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.HotelManagementSystem_APi.dto.IncidentalRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.IncidentalResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.StayRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.StayResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.StaySummaryDTO;
import com.reis.HotelManagementSystem_APi.entities.Incidental;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Stay;
import com.reis.HotelManagementSystem_APi.repositories.IncidentalRepository;
import com.reis.HotelManagementSystem_APi.repositories.StayRepository;
import com.reis.HotelManagementSystem_APi.services.exceptions.CheckInDateException;
import com.reis.HotelManagementSystem_APi.services.exceptions.InvalidActionException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class StayService {

	@Autowired
	private StayRepository repository;
	
	@Autowired
	private IncidentalRepository incidentalRepository;
	
	@Autowired
	private ReservationService reservationService;
	
	public List<StaySummaryDTO> findAll(){
		List<Stay> list = repository.findAll();
		
		List<StaySummaryDTO> resp = list.stream().map(StaySummaryDTO::new).collect(Collectors.toList());
		
		return resp;
	}
	
	public StayResponseDTO findById(Long id) {
		Stay obj = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
		return new StayResponseDTO(obj);
	}
	
	@Transactional
	public StayResponseDTO checkIn(StayRequestDTO dto) {
		Reservation reservation = reservationService.checkInStay(dto.getReservationId());
		
		if (!reservation.getCheckInDate().isEqual(dto.getCheckInDate().toLocalDate())) {
			throw new CheckInDateException(dto.getCheckInDate(), reservation.getCheckInDate());
		}
		
		Stay obj = new Stay(dto.getCheckInDate(), dto.getCheckOutDate(), reservation);
		obj = repository.save(obj);
		
		return new StayResponseDTO(obj);
	}
	
	@Transactional
	public StayResponseDTO checkOut (Long id) {
		Stay obj = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
		obj.setCheckOutDate(LocalDateTime.now());
		
		reservationService.peformCheckOut(obj.getReservation().getId(), obj.getCheckOutDate().toLocalDate());
		obj = repository.save(obj);
		return new StayResponseDTO(obj);
	}
	
	@Transactional
	public IncidentalResponseDTO addIncidental (Long id, IncidentalRequestDTO dto) {
		Stay obj = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
		if(obj.getCheckOutDate() != null) {
			throw new InvalidActionException("Não é possivel adicionar um consumo em uma estadia finalizada");
		}
		
		Incidental incidental = new Incidental(dto.getName(), dto.getQuantity(), dto.getPrice(), (dto.getMoment() != null)? dto.getMoment(): LocalDateTime.now(), obj);
		incidentalRepository.save(incidental);
		return new IncidentalResponseDTO(incidental);
	}
	
}
