package com.reis.HotelManagementSystem_APi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.HotelManagementSystem_APi.dto.StayResponseDTO;
import com.reis.HotelManagementSystem_APi.entities.Stay;
import com.reis.HotelManagementSystem_APi.repositories.ReservationRepository;
import com.reis.HotelManagementSystem_APi.repositories.StayRepository;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

@Service
public class StayService {

	@Autowired
	private StayRepository repository;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	public List<StayResponseDTO> findAll(){
		List<Stay> list = repository.findAll();
		
		List<StayResponseDTO> resp = list.stream().map(StayResponseDTO::new).collect(Collectors.toList());
		
		return resp;
	}
	
	public StayResponseDTO findById(Long id) {
		Stay obj = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(id));
		return new StayResponseDTO(obj);
	}
	
}
