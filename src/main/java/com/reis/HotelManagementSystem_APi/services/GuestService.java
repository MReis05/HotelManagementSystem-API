package com.reis.HotelManagementSystem_APi.services;

import java.util.List;

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
}
