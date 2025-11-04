package com.reis.HotelManagementSystem_APi.entities.enums;

public enum RoomType {

	SOLTEIRO(1, "Uma cama de solteiro"),
	CASAL(2, "Uma cama de casal"),
	TRIPLO(3, "Uma cama de casal e uma de solteiro"),
	QUADRUPLO_SOLTEIRO(4, "Uma cama de casal e duas de solteiro"),
	QUADRUPLO_CASAL(5, "Duas camas de casal"),
	QUINTUPLO(6, "Duas camas de casal e uma de solteiro");
	
	private final int id;
	private final String description;
	
	RoomType(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
	
	public static RoomType valueOf(int id) {
		for (RoomType value : RoomType.values()) {
			if (value.getId() == id) {
				return value;
			}
		}
		throw new IllegalArgumentException("Id de Type invalido: " + id);
	}
}
