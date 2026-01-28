package com.reis.HotelManagementSystem_APi.Repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
		
		List<Room> listReceveid = repository.findByStatus(persitedRoom.getStatus());
		
		assertNotNull(listReceveid);
		assertEquals(1, listReceveid.size());
		assertEquals(RoomStatus.DISPONIVEL, listReceveid.get(0).getStatus());
	}
	
	private Room createStandardRoom() {
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		return r1;
	}
}
