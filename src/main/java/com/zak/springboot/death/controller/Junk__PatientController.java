package com.zak.springboot.death.controller;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zak.springboot.death.service.Junk__PatientService;

@RestController
@RequestMapping("/")
public class Junk__PatientController {
	@Autowired
	private Junk__PatientService patientService;
	
	// http://localhost:3000/deaths/	
	@GetMapping("/deaths")	//<==================================== "GET" request
//	@RequestMapping("/deaths")
	public Bundle allPatients() {
		return patientService.getAllPatients();				
//		return "Hello from the 'death' PetAllPatients()";				
	}
		
	// http://localhost:3000/getbundle/	
	@GetMapping("/bundle")	//<==================================== "GET" request
//	@RequestMapping("/bundle")
//	public Bundle bundle() {
	public String bundle() {
//		patientService.getbundle();				
		return patientService.getbundle();				
	}
		
	// http://localhost:3000/Patient/	
	@GetMapping("/FHIR/Patient")	//<==================================== "GET" request
	public Bundle patient() {
//		return patientService.getAllPatients();				
//		return "Hello from the 'death' PetAllPatients()";	
		return null;
	}
		
	
	// http://localhost:5151/patient/123
//	@GetMapping("/deaths/{id}")
//	public ResponseEntity<Topic> getPatient(@PathVariable String id) {		
//		return new ResponseEntity<Topic>(patientService.getPatient(id), HttpStatus.OK);
//	}

	@PostMapping("/deaths") //<==== "POST" ADD new request
//	public ResponseEntity<Object> addPatient(@RequestBody Patient patient) {
	public ResponseEntity<Object> addPatient() {
		patientService.addPatient();
		return new ResponseEntity<Object>("POST Response", HttpStatus.OK);
//		Patient patient = patientService.addPatient();
//		return new ResponseEntity<Object>(patient, HttpStatus.OK);

	}

}
