package com.reis.HotelManagementSystem_APi.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_incidental")
public class Incidental implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private Integer quantity;
	@Column(precision = 10, scale = 2)
	private BigDecimal price;
	private LocalDateTime moment;
	
	@ManyToOne
	@JoinColumn(name = "stay_id")
	private Stay stay;
	
	public Incidental() {
	}

	public Incidental(String name, Integer quantity, BigDecimal price, LocalDateTime moment, Stay stay) {
		super();
		this.name = name;
		this.quantity = quantity;
		this.price = price;
		this.moment = moment;
		this.stay = stay;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public LocalDateTime getMoment() {
		return moment;
	}

	public void setMoment(LocalDateTime moment) {
		this.moment = moment;
	}

	public Long getId() {
		return id;
	}
	
	public Stay getStay() {
		return stay;
	}

	public void setStay(Stay stay) {
		this.stay = stay;
	}

	public BigDecimal totalValue() {
		return price.multiply(BigDecimal.valueOf(quantity));
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Incidental other = (Incidental) obj;
		return Objects.equals(id, other.id);
	}
}
