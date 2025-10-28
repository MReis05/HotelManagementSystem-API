package com.reis.HotelManagementSystem_APi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.services.GuestService;

@RestController
@RequestMapping(value ="/guest")
public class GuestController {
	
	@Autowired
	private GuestService service;
	
	@GetMapping
	public ResponseEntity<List<Guest>> findAll(){
		List<Guest> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}

}
