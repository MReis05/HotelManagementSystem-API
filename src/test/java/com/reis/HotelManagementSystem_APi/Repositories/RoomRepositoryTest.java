package com.reis.HotelManagementSystem_APi.Repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;

@DataJpaTest
@ActiveProfiles("test")
public class RoomRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private RoomRepository repository;
	
	@Test
	@DisplayName("Should return a List of Room when finding by existing status")
	void findByStatusSuccessCase() {
		Room room = createStandardRoom();
		
		Room persitedRoom = entityManager.persistAndFlush(room);
		
		List<Room> listReceived = repository.findByStatus(persitedRoom.getStatus());
		
		assertNotNull(listReceived);
		assertEquals(1, listReceived.size());
		assertEquals(RoomStatus.DISPONIVEL, listReceived.get(0).getStatus());
	}
	
	@Test
	@DisplayName("Should return an empty list when Room Status is not found")
	void findByStatusNotFoundCase() {
		List<Room> listReceived = repository.findByStatus(RoomStatus.MANUTENCAO);
		
		assertNotNull(listReceived);
		assertTrue(listReceived.isEmpty());
	}
	
	@Test
	@DisplayName("Should return a List of Room when finding by existing type")
	void findByTypeSuccessCase() {
		Room room = createStandardRoom();
		
		Room persitedRoom = entityManager.persistAndFlush(room);
		
		List<Room> listReceived = repository.findByType(persitedRoom.getType());
		
		assertNotNull(listReceived);
		assertEquals(RoomType.SOLTEIRO, listReceived.get(0).getType());
	}
	
	@Test
	@DisplayName("Should return an empty list when Room Type is not found")
	void findByTypeNotFoundCase() {
		List<Room> listReceived = repository.findByType(RoomType.QUADRUPLO_CASAL);
		
		assertNotNull(listReceived);
		assertTrue(listReceived.isEmpty());
	}
	
	@Test
	@DisplayName("Should return a List of Room when finding by existing type and status")
	void findByTypeAndStatusSuccessCase() {
		Room room = createStandardRoom();
		
		Room persitedRoom = entityManager.persistAndFlush(room);
		
		List<Room> listReceived = repository.findByTypeAndStatus(persitedRoom.getType(), persitedRoom.getStatus());
		
		assertNotNull(listReceived);
		assertEquals(RoomType.SOLTEIRO, listReceived.get(0).getType());
		assertEquals(RoomStatus.DISPONIVEL, listReceived.get(0).getStatus());
	}
	
	@Test
	@DisplayName("Should return an empty list when Room Type and Status are not found")
	void findByTypeAndStatusNotFoundCase() {
		List<Room> listReceived = repository.findByTypeAndStatus(RoomType.CASAL, RoomStatus.MANUTENCAO);
		
		assertNotNull(listReceived);
		assertTrue(listReceived.isEmpty());
	}
	
	private Room createStandardRoom() {
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		return r1;
	}
}
