package com.reis.HotelManagementSystem_APi.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;

@Service
public class GuestService {
	
	@Autowired
	private GuestRepository repository;
	
	public List<Guest> findAll(){
		List<Guest> list = repository.findAll();
		return list;
	}
	
	public Guest findById (Long id) {
		Optional<Guest> obj = repository.findById(id);
		return obj.orElseThrow();
	}
	
	public Guest insert (Guest obj) {
		return repository.save(obj);
	}
	
	public void delete (Long id) {
		repository.deleteById(id);
	}
	
	public Guest update (Long id, Guest newObj) {
		Guest obj = repository.findById(id).orElseThrow();
		
		updateData(newObj, obj);
		
		return repository.save(obj);
	}

	private void updateData(Guest newObj, Guest obj) {
		if(newObj.getName() != null){
			obj.setName(newObj.getName());
		}
		if(newObj.getEmail() != null) {
			obj.setEmail(newObj.getEmail());
		}
		if(newObj.getPhone() != null) {
			obj.setPhone(newObj.getPhone());
		}
		if(newObj.getBirthDate() != null) {
			obj.setBirthDate(newObj.getBirthDate());
		}
		if(newObj.getAddress() != null) {
			obj.setAddress(newObj.getAddress());
		}
	}
}
