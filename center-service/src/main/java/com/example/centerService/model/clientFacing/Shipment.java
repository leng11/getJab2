package com.example.centerService.model.clientFacing;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
public class Shipment {
	@Getter
	private long vaccineId;
	
	@Getter
	private String lot;
	
	@Getter
	private int lotSize;
	
	@Getter
	private Date expiration;
}
