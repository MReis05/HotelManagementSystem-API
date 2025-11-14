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

import com.reis.HotelManagementSystem_APi.dto.ReservationRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationResponseDTO;
import com.reis.HotelManagementSystem_APi.services.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/reservation")
public class ReservationController {

	@Autowired
	private ReservationService service;
	
	@GetMapping
	public ResponseEntity<List<ReservationResponseDTO>> findReservation(@RequestParam(required = false) String status){
		List<ReservationResponseDTO> dto;
		if (status != null && !status.isEmpty()) {
			dto = service.findByStatus(status);
		}
		else {
			dto = service.findAll();
		}
		return ResponseEntity.ok().body(dto);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<ReservationResponseDTO> findById (@PathVariable Long id){
		ReservationResponseDTO dto = service.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	@PostMapping
	public ResponseEntity<ReservationResponseDTO> insert(@Valid @RequestBody ReservationRequestDTO dto){
		ReservationResponseDTO resp = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("{id}").buildAndExpand(resp.getId()).toUri();
		return ResponseEntity.created(uri).body(resp);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<ReservationResponseDTO> updateStatus(@PathVariable Long id, @RequestParam String action, @RequestBody ReservationRequestDTO dto){
		ReservationResponseDTO resp = service.updateStatus(id, action, dto);
		return ResponseEntity.ok().body(resp);
	}
}
