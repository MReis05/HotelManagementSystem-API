package com.reis.HotelManagementSystem_APi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.reis.HotelManagementSystem_APi.dto.RoomCreateDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomUpdateDTO;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;
import com.reis.HotelManagementSystem_APi.services.exceptions.DatabaseException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

@Service
public class RoomService {

	@Autowired
	private RoomRepository repository;
	
	public List<RoomResponseDTO> findAll() {
		List<Room> list = repository.findAll();
		List<RoomResponseDTO> dto = list.stream().map(RoomResponseDTO::new).collect(Collectors.toList());
		return dto;
	}
	
	public RoomResponseDTO findById(Long id) {
		Room obj = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
		return new RoomResponseDTO(obj);
	}
	
	public RoomResponseDTO insert (RoomCreateDTO dto) {
		Room obj = new Room(dto.getNumber(), dto.getPricePerNight(), dto.getDescription(), dto.getStatus(), dto.getType());
		obj = repository.save(obj);
		return new RoomResponseDTO(obj);
	}
	
	public RoomResponseDTO update (Long id, RoomUpdateDTO dto) {
		Room obj = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
		
		updateData(obj, dto);
		
		obj = repository.save(obj);
		return new RoomResponseDTO(obj);
	}
	
	public void delete (Long id) {
		
		if(!repository.existsById(id)) {
			throw new ResourceNotFoundException(id);
		}
		
		try {
			repository.deleteById(id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	private void updateData(Room obj, RoomUpdateDTO dto) {
		if (dto.getDescription() != null) {
			obj.setDescription(dto.getDescription());
		}
		if (dto.getPricePerNight() != null) {
			obj.setPricePerNight(dto.getPricePerNight());
		}
		if (dto.getStatus() != null) {
			obj.setStatus(dto.getStatus());
		}
		
	}
}
