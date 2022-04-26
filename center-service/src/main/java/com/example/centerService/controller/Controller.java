package com.example.centerService.controller;


import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class Controller {
	public static final String ADD_CENTER_URL = "/v1/vaccineCenters/addCenter";
	public static final String DELETE_CENTER_BY_NAME_URL = "/v1/vaccineCenters/deleteCenterByName/{name}";
	public static final String LIST_CENTER_URL = "/v1/vaccineCenters/listCenter";
	public static final String ADD_VACCINE_URL = "/v1/vaccineCenters/addVaccine";
	public static final String DELETE_VACCINE_BY_NAME_URL = "/v1/vaccineCenters/deleteVaccineByName/{name}";
	public static final String LIST_VACCINE_URL = "/v1/vaccineCenters/listVaccine";
	public static final String RESTOCK_URL = "/v1/vaccineCenters/restock";
	public static final String PUBLISH_REMINDER_URL = "/v1/vaccineCenters/publishReminder";
	
	@Autowired
	Service service;
	
	@PostMapping(ADD_CENTER_URL)
	public Center add(@Valid @RequestBody Center center) {
			return service.add(center);
	}
	
	@DeleteMapping(DELETE_CENTER_BY_NAME_URL)
	public int deleteCenterByName(@PathVariable final String name) {
		return service.deleteCenterByName(name);
	}
	
	@GetMapping(LIST_CENTER_URL)
	public Center[] listCenter() {
		return service.listCenter().toArray(new Center[0]);
	}
	
	@PostMapping(ADD_VACCINE_URL)
	public Vaccine add(@RequestBody Vaccine vaccine) {
		return service.add(vaccine);
	}
	
	@DeleteMapping(DELETE_VACCINE_BY_NAME_URL)
	public int deleteVaccineByName(@PathVariable final String name) {
		return service.deleteVaccineByName(name);
	}
	
	@GetMapping(LIST_VACCINE_URL)
	public Vaccine[] listVaccine() {
		return service.listVaccine().toArray(new Vaccine[0]);
	}
	
	@PostMapping(RESTOCK_URL)
	public Inventory restock(@RequestBody Shipment shipment) {
		return service.restock(shipment);	
	}
	
	@PutMapping(PUBLISH_REMINDER_URL)
	public Boolean publishReminder(@RequestParam("date") 
	  								@DateTimeFormat(pattern = "yyyy-mm-dd") Date date) {
		return service.publishReminder(date);
	}

	// This is to facilitate testing (not a public API.
	@PutMapping("/v1/vaccineCenters/shotAdministrated/{inventoryId}/{vaccineId}/{lot}/{userId}")
	public boolean testShotAdministratedFeature(@PathVariable final long inventoryId,
												@PathVariable final long vaccineId,
												@PathVariable final String lot,
												@PathVariable final long userId) {
		return service.testShotAdministratedFeature(inventoryId, vaccineId, lot, userId);
	}

}
