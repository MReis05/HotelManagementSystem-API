package com.reis.HotelManagementSystem_APi.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.reis.HotelManagementSystem_APi.dto.IncidentalRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.IncidentalResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.StayRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.StayResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.StaySummaryDTO;
import com.reis.HotelManagementSystem_APi.services.StayService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value ="/stay")
public class StayController {

	@Autowired
	private StayService service;
	
	@GetMapping
	public ResponseEntity<List<StaySummaryDTO>> findAll(){
		List<StaySummaryDTO> resp = service.findAll();
		return ResponseEntity.ok().body(resp);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<StayResponseDTO> findById(@PathVariable Long id){
		StayResponseDTO resp = service.findById(id);
		return ResponseEntity.ok().body(resp);
	}
	
	@PostMapping
	public ResponseEntity<StayResponseDTO> checkIn (@Valid @RequestBody StayRequestDTO dto){
		StayResponseDTO resp = service.checkIn(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(resp.getId()).toUri();
		return ResponseEntity.created(uri).body(resp);
	}
	
	@PutMapping(value = "/{id}/checkOut")
	public ResponseEntity<StayResponseDTO> checkOut(@RequestParam Long id){
		StayResponseDTO resp = service.checkOut(id);
		return ResponseEntity.ok().body(resp);
	}
	
	@PutMapping(value = "/incidental")
	public ResponseEntity<IncidentalResponseDTO> addIncidental(@RequestParam Long stayId, @RequestBody IncidentalRequestDTO dto){
		IncidentalResponseDTO resp = service.addIncidental(stayId, dto);
		return ResponseEntity.ok().body(resp);
	}
}

