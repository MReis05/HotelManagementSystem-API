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

import com.reis.HotelManagementSystem_APi.dto.RoomCreateDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomResponseDTO;
import com.reis.HotelManagementSystem_APi.dto.RoomUpdateDTO;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.services.RoomService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/room")
public class RoomController {

	@Autowired
	private RoomService service;
	
	@GetMapping
	public ResponseEntity<List<RoomResponseDTO>> findAll(){
		List<Room> list = service.findAll();
		List<RoomResponseDTO> dto = list.stream().map(RoomResponseDTO::new).collect(Collectors.toList());
		return ResponseEntity.ok().body(dto);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<RoomResponseDTO> findById(@PathVariable Long id){
		Room obj = service.findById(id);
		RoomResponseDTO dto = new RoomResponseDTO(obj);
		return ResponseEntity.ok().body(dto);
	}
	
	@PostMapping
	public ResponseEntity<RoomResponseDTO> insert (@Valid @RequestBody RoomCreateDTO dto){
		Room obj = service.insert(dto);
		RoomResponseDTO resp = new RoomResponseDTO(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("{id}").buildAndExpand(resp.getId()).toUri();
		return ResponseEntity.created(uri).body(resp);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<RoomResponseDTO> update (@PathVariable Long id, @Valid @RequestBody RoomUpdateDTO dto){
		Room obj = service.update(id, dto);
		RoomResponseDTO resp = new RoomResponseDTO(obj);
		return ResponseEntity.ok().body(resp);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete (@PathVariable Long id){
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
