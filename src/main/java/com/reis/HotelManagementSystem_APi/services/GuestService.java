package com.reis.HotelManagementSystem_APi.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.dto.AddressDTO;
import com.reis.HotelManagementSystem_APi.entities.dto.GuestRequestDTO;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;

@Service
public class GuestService {

	@Autowired
	private GuestRepository repository;

	public List<Guest> findAll() {
		List<Guest> list = repository.findAll();
		return list;
	}

	public Guest findById(Long id) {
		Optional<Guest> obj = repository.findById(id);
		return obj.orElseThrow();
	}

	public Guest insert(GuestRequestDTO dto) {
		Address address = new Address(dto.getAddress().getCep(), dto.getAddress().getUf(), dto.getAddress().getCity(),
				dto.getAddress().getNeighborhood(), dto.getAddress().getStreet(), dto.getAddress().getHouseNumber());
		Guest obj = new Guest(dto.getName(), dto.getCpf(), dto.getEmail(), dto.getPhone(), dto.getBirthDate(), address);
		return repository.save(obj);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	public Guest update(Long id, GuestRequestDTO dto) {
		Guest obj = repository.findById(id).orElseThrow();

		updateData(dto, obj);

		return repository.save(obj);
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
