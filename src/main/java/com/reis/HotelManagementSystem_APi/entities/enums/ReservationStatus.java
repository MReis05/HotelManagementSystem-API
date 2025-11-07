package com.reis.HotelManagementSystem_APi.entities.enums;

public enum ReservationStatus {

	CONFIRMADA(1, "Reserva confirmada"),
	PENDENTE(2, "Reserva pendente"),
	CANCELADA(3, "Reserva cancelada"),
	CONCLUIDA(4, "Reserva concluída");
	
	private final int id;
	private final String description;
	
	ReservationStatus(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
}
