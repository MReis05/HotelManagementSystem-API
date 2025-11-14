package com.reis.HotelManagementSystem_APi.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.reis.HotelManagementSystem_APi.dto.RoomCreateDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomUpdateDTO;
import com.reis.HotelManagementSystem_APi.services.RoomService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/room")
public class RoomController {

	@Autowired
	private RoomService service;
	
	@GetMapping
	public ResponseEntity<List<RoomResponseDTO>> findRooms(@RequestParam(required = false) String type, @RequestParam(required = false) String status){
		List<RoomResponseDTO> dto;
		boolean statusCondition = status != null && !status.isEmpty();
		boolean typeCondition =  type != null && !type.isEmpty();
		
		if(statusCondition && typeCondition) {
			dto = service.findByTypeAndStatus(type, status);
		}
		else if (statusCondition) {
			dto = service.findByStatus(status);
		}
		else if (typeCondition){
			dto = service.findByType(type);
		}
		else {
			dto = service.findAll();
		}
		return ResponseEntity.ok().body(dto);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<RoomResponseDTO> findById(@PathVariable Long id){
		RoomResponseDTO dto = service.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	@PostMapping
	public ResponseEntity<RoomResponseDTO> insert (@Valid @RequestBody RoomCreateDTO dto){
		RoomResponseDTO resp = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("{id}").buildAndExpand(resp.getId()).toUri();
		return ResponseEntity.created(uri).body(resp);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<RoomResponseDTO> update (@PathVariable Long id, @Valid @RequestBody RoomUpdateDTO dto){
		RoomResponseDTO resp = service.update(id, dto);
		return ResponseEntity.ok().body(resp);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete (@PathVariable Long id){
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
