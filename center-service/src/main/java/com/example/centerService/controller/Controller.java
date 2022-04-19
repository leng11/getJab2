package com.example.centerService.controller;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.centerService.model.Center;
import com.example.centerService.model.Inventory;
import com.example.centerService.model.Vaccine;
import com.example.centerService.model.clientFacing.Shipment;
import com.example.centerService.service.Service;

@RestController
public class Controller {
	
	@Autowired
	Service service;
	
	@PostMapping("/v1/vaccineCenters/addCenter")
	public Center add(@RequestBody Center center) {
		return service.add(center);
	}
	
	@GetMapping("/v1/vaccineCenters/listCenter")
	public List<Center> listCenter() {
		return service.listCenter();
	}
	
	@PostMapping("/v1/vaccineCenters/addVaccine")
	public Vaccine add(@RequestBody Vaccine vaccine) {
		return service.add(vaccine);
	}
	
	@GetMapping("/v1/vaccineCenters/listVaccine")
	public List<Vaccine> listVaccine() {
		return service.listVaccine();
	}
	
	@PostMapping("/v1/vaccineCenters/restock")
	public Inventory restock(@RequestBody Shipment shipment) {
		return service.restock(shipment);	
	}
	
	@PutMapping("/v1/vaccineCenters/publishReminder")
	public Boolean publishReminder(@RequestParam("date") 
	  								@DateTimeFormat(pattern = "dd-MM-yyyy") Date date) {
		return service.publishReminder(date);
	}

	@PutMapping("/v1/vaccineCenters/shotAdministrated/{inventoryId}/{vaccineId}/{lot}/{userId}")
	public boolean testShotAdministratedFeature(@PathVariable final long inventoryId,
												@PathVariable final long vaccineId,
												@PathVariable final String lot,
												@PathVariable final long userId) {
		return service.testShotAdministratedFeature(inventoryId, vaccineId, lot, userId);
	}

}
