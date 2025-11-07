package com.reis.HotelManagementSystem_APi.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.reis.HotelManagementSystem_APi.dto.GuestRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.GuestResponseDTO;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.services.GuestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value ="/guest")
public class GuestController {
	
	@Autowired
	private GuestService service;
	
	@GetMapping
	public ResponseEntity<List<GuestResponseDTO>> findAll(){
		List<Guest> list = service.findAll();
		List<GuestResponseDTO> dtoList = list.stream().map(GuestResponseDTO::new).collect(Collectors.toList());
		return ResponseEntity.ok().body(dtoList);
	}
	
	@GetMapping(value ="/{id}")
	public ResponseEntity<GuestResponseDTO> findById (@PathVariable Long id){
		Guest obj = service.findById(id);
		GuestResponseDTO dto = new GuestResponseDTO(obj);
		return ResponseEntity.ok().body(dto);
	}
	
	@PostMapping
	public ResponseEntity<GuestResponseDTO> insert (@RequestBody @Valid GuestRequestDTO dto){
		Guest obj = service.insert(dto);
		GuestResponseDTO resp = new GuestResponseDTO(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("{id}").buildAndExpand(resp.getId()).toUri();
		return ResponseEntity.created(uri).body(resp);
	}
	
	@DeleteMapping(value="/{id}")
	public ResponseEntity<Void> delete (@PathVariable Long id){
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@PutMapping(value ="/{id}")
	public ResponseEntity<GuestResponseDTO> update(@PathVariable Long id, @RequestBody GuestRequestDTO dto){
		Guest obj = service.update(id, dto);
		GuestResponseDTO resp = new GuestResponseDTO(obj);
		return ResponseEntity.ok().body(resp);
	}
}
