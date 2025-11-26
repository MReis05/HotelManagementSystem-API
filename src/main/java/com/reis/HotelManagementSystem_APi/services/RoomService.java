package com.reis.HotelManagementSystem_APi.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.reis.HotelManagementSystem_APi.dto.RoomCreateDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomUpdateDTO;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;
import com.reis.HotelManagementSystem_APi.services.exceptions.DatabaseException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

import jakarta.transaction.Transactional;

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
	
	public List<RoomResponseDTO> findByStatus(String status){
		RoomStatus roomStatus;
		try {
			roomStatus = RoomStatus.valueOf(status.toUpperCase());
		}
		catch(IllegalArgumentException e) {
			return Collections.emptyList();
		}
		
		List<Room> list = repository.findByStatus(roomStatus);
		List<RoomResponseDTO> dto = list.stream().map(RoomResponseDTO::new).collect(Collectors.toList());
		return dto;
	}
	
	public List<RoomResponseDTO> findByType(String type){
		RoomType roomType;
		try {
			roomType = RoomType.valueOf(type.toUpperCase());
		}
		catch(IllegalArgumentException e) {
			return Collections.emptyList();
		}
		
		List<Room> list = repository.findByType(roomType);
		List<RoomResponseDTO> dto = list.stream().map(RoomResponseDTO::new).collect(Collectors.toList());
		return dto;
	}

	
	public List<RoomResponseDTO> findByTypeAndStatus(String type, String status){
		RoomStatus roomStatus;
		RoomType roomType;
		try {
			roomStatus = RoomStatus.valueOf(status.toUpperCase());
			roomType = RoomType.valueOf(type.toUpperCase());
		}
		catch(IllegalArgumentException e) {
			return Collections.emptyList();
		}
		
		List<Room> list = repository.findByTypeAndStatus(roomType, roomStatus);
		List<RoomResponseDTO> dto = list.stream().map(RoomResponseDTO::new).collect(Collectors.toList());
		return dto;
	}
	
	@Transactional
	public RoomResponseDTO insert (RoomCreateDTO dto) {
		Room obj = new Room(dto.getNumber(), dto.getPricePerNight(), dto.getDescription(), dto.getStatus(), dto.getType());
		obj = repository.save(obj);
		return new RoomResponseDTO(obj);
	}
	
	@Transactional
	public RoomResponseDTO update (Long id, RoomUpdateDTO dto) {
		Room obj = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
		
		updateData(obj, dto);
		
		obj = repository.save(obj);
		return new RoomResponseDTO(obj);
	}
	
	@Transactional
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
