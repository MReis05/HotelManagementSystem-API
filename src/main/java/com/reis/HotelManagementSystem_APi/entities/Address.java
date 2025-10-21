package com.reis.HotelManagementSystem_APi.entities;

public class Address {

	private String cep;
	private String uf;
	private String city;
	private String neighborhood;
	private String street;
	private Integer houseNumber;
	
	public Address () {
	}

	public Address(String cep, String uf, String city, String neighborhood, String street, Integer houseNumber) {
		super();
		this.cep = cep;
		this.uf = uf;
		this.city = city;
		this.neighborhood = neighborhood;
		this.street = street;
		this.houseNumber = houseNumber;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Integer getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(Integer houseNumber) {
		this.houseNumber = houseNumber;
	}
}
