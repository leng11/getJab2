package com.example.centerService.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.centerService.model.clientFacing.Shipment;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "vaccineId")
	@Getter
	private Vaccine vaccine;
	
	@Getter
	private String lot;
	
	@Getter
	private int lotSize;
	
	@Getter
	@Setter
	private int available;
	
	@Getter
	private Date expiration;
	
	@ManyToOne
    @JsonBackReference
    @JoinColumn(name = "centerId")
	@Getter
	@Setter
	private Center center;
	
	public static Inventory of(final Shipment shipment) {
		Inventory inventory = new Inventory();
		inventory.setLot(shipment.getLot());
		inventory.setLotSize(shipment.getLotSize());
		inventory.setExpiration(shipment.getExpiration());
		inventory.setAvailable(shipment.getLotSize());
		return inventory;
	}
}
