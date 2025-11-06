package com.reis.HotelManagementSystem_APi.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.dto.RoomCreateDTO;
import com.reis.HotelManagementSystem_APi.entities.dto.RoomUpdateDTO;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;
import com.reis.HotelManagementSystem_APi.services.exceptions.DatabaseException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

@Service
public class RoomService {

	@Autowired
	private RoomRepository repository;
	
	public List<Room> findAll() {
		return repository.findAll();
	}
	
	public Room findById(Long id) {
		Optional<Room> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ResourceNotFoundException(id));
	}
	
	public Room insert (RoomCreateDTO dto) {
		Room obj = new Room(dto.getNumber(), dto.getPricePerNight(), dto.getDescription(), dto.getStatus(), dto.getType());
		return repository.save(obj);
	}
	
	public Room update (Long id, RoomUpdateDTO dto) {
		Room obj = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
		updateData(obj, dto);
		return repository.save(obj);
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
