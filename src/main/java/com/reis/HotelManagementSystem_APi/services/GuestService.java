package com.reis.HotelManagementSystem_APi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.reis.HotelManagementSystem_APi.dto.AddressDTO;
import com.reis.HotelManagementSystem_APi.dto.GuestRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.GuestResponseDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.services.exceptions.DatabaseException;
import com.reis.HotelManagementSystem_APi.services.exceptions.ResourceNotFoundException;

@Service
public class GuestService {

	@Autowired
	private GuestRepository repository;

	public List<GuestResponseDTO> findAll() {
		List<Guest> list = repository.findAll();
		List<GuestResponseDTO> dtoList = list.stream().map(GuestResponseDTO::new).collect(Collectors.toList());
		return dtoList;
	}

	public GuestResponseDTO findById(Long id) {
		Guest obj = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
		GuestResponseDTO dto = new GuestResponseDTO(obj);
		return dto;
	}

	public GuestResponseDTO insert(GuestRequestDTO dto) {
		Address address = new Address(dto.getAddress().getCep(), dto.getAddress().getUf(), dto.getAddress().getCity(),
				dto.getAddress().getNeighborhood(), dto.getAddress().getStreet(), dto.getAddress().getHouseNumber());
		Guest obj = new Guest(dto.getName(), dto.getCpf(), dto.getEmail(), dto.getPhone(), dto.getBirthDate(), address);
		
		obj = repository.save(obj);
		return new GuestResponseDTO(obj);
	}

	public void delete(Long id) {
		
		if(!repository.existsById(id)) {
			throw new ResourceNotFoundException(id);
		}
		
		try {
			repository.deleteById(id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public GuestResponseDTO update(Long id, GuestRequestDTO dto) {
		Guest obj = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));

		updateData(dto, obj);
		
		obj = repository.save(obj);

		return new GuestResponseDTO(obj);
	}

	private void updateData(GuestRequestDTO dto, Guest obj) {
		if (dto.getName() != null) {
			obj.setName(dto.getName());
		}
		if (dto.getEmail() != null) {
			obj.setEmail(dto.getEmail());
		}
		if (dto.getPhone() != null) {
			obj.setPhone(dto.getPhone());
		}
		if (dto.getBirthDate() != null) {
			obj.setBirthDate(dto.getBirthDate());
		}
		if (dto.getAddress() != null) {
			updateAddress(obj.getAddress(), dto.getAddress());
		}
	}
	
	private void updateAddress(Address address, AddressDTO dto) {
		if(dto.getCep() != null) {
			address.setCep(dto.getCep());
		}
		if(dto.getCity() != null) {
			address.setCity(dto.getCity());
		}
		if(dto.getUf() != null) {
			address.setUf(dto.getUf());
		}
		if(dto.getNeighborhood() != null) {
			address.setNeighborhood(dto.getNeighborhood());
		}
		if(dto.getStreet() != null) {
			address.setStreet(dto.getStreet());
		}
		if(dto.getHouseNumber() != null) {
			address.setHouseNumber(dto.getHouseNumber());
		}
	}
}
