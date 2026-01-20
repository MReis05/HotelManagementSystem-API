package com.reis.HotelManagementSystem_APi.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.reis.HotelManagementSystem_APi.dto.PaymentRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.ReservationSummaryDTO;
import com.reis.HotelManagementSystem_APi.services.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/reservations")
public class ReservationController {

	@Autowired
	private ReservationService service;
	
	@GetMapping
	public ResponseEntity<List<ReservationSummaryDTO>> findReservations(@RequestParam(required = false) String status){
		List<ReservationSummaryDTO> dto;
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
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(resp.getId()).toUri();
		return ResponseEntity.created(uri).body(resp);
	}
	
	@PutMapping(value = "/{id}/update")
	public ResponseEntity<ReservationResponseDTO> updateReservation(@PathVariable Long id, @RequestBody ReservationRequestDTO dto){
		ReservationResponseDTO resp = service.updateReservation(id, dto);
		return ResponseEntity.ok().body(resp);
	}
	
	@PatchMapping(value = "/{id}/cancel")
	public ResponseEntity<ReservationResponseDTO> cancelReservation(@PathVariable Long id){
		ReservationResponseDTO resp = service.cancelReservation(id);
		return ResponseEntity.ok().body(resp);
	}
	
	@PutMapping(value = "/{id}/payment")
	public ResponseEntity<ReservationResponseDTO> confirmReservation (@PathVariable Long id, @Valid @RequestBody PaymentRequestDTO dto){
		ReservationResponseDTO resp = service.confirmReservation(id, dto);
		return ResponseEntity.ok().body(resp);
	}
}
