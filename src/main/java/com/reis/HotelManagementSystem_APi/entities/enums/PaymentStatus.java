package com.reis.HotelManagementSystem_APi.entities.enums;

public enum PaymentStatus {

	PENDENTE(1),
	RECUSADO(2),
	APROVADO(3);
	
	private Integer id;
	
	PaymentStatus(int id) {
		this.id= id;
	}

	public Integer getId() {
		return id;
	}
	
	public static PaymentStatus valueOf(int id) {
		for (PaymentStatus value: PaymentStatus.values()) {
			if(value.getId() == id) {
				return value;
			}
		}
		 throw new IllegalArgumentException("ID de Status inválido: " + id);
	}
}
