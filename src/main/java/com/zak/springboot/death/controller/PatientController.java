package com.zak.springboot.death.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zak.springboot.death.service.PatientService;

@RestController
@RequestMapping("/")
public class PatientController {
	
	@Autowired
	private PatientService patientService;
	
	// http://localhost:3000/Patient/	
	@GetMapping("/Patient")	//<==================================== "GET" request
	public String patient() {
		return patientService.getPatients();
	}
	
}
