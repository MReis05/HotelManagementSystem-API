package com.reis.HotelManagementSystem_APi.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
	public ResponseEntity<List<ReservationResponseDTO>> findAll(){
		List<ReservationResponseDTO> dto = service.findAll();
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
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
