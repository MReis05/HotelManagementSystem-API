package com.reis.HotelManagementSystem_APi.entities.enums;

public enum PaymentType {

	DINHEIRO(1),
	PIX(2),
	CARTAO_DE_CREDITO(3),
	CARTAO_DE_DEBITO(4);
	
	private Integer id;
	
	PaymentType(int id){
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
	
	public static PaymentType valueOf(int id) {
		for (PaymentType value : PaymentType.values()) {
			if(value.getId() == id) {
				return value;
			}
		}
		 throw new IllegalArgumentException("ID de Status inválido: " + id);
	}
}
