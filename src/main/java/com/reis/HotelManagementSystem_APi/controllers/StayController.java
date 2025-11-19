package com.reis.HotelManagementSystem_APi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reis.HotelManagementSystem_APi.dto.StayResponseDTO;
import com.reis.HotelManagementSystem_APi.services.StayService;

@RestController
@RequestMapping(value ="/stay")
public class StayController {

	@Autowired
	private StayService service;
	
	@GetMapping
	public ResponseEntity<List<StayResponseDTO>> findAll(){
		List<StayResponseDTO> resp = service.findAll();
		return ResponseEntity.ok().body(resp);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<StayResponseDTO> findById(@PathVariable Long id){
		StayResponseDTO resp = service.findById(id);
		return ResponseEntity.ok().body(resp);
	}
}

