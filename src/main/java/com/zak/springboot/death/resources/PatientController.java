package com.zak.springboot.death.resources;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zak.springboot.death.RequestedParameters;
import com.zak.springboot.death.services.PatientService;

@RestController
@RequestMapping("/fhir")
public class PatientController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PatientService patientService;
	
	private String stateFileNumber = "";	// [FHIR:Identifier][EDEN:stateFileNumber], 
	private String deceasedFirst = "";		// [FHIR:given][EDEN:deceasedFirst], first name
	private String deceasedLast = "";		// [FHIR:family][EDEN:deceasedLast]

	private String format = "json";			// "json", "xml"
	
	
	// incoming parameter names is based on FHIR naming standard
	@GetMapping("/Patient")	//<===================================================================================== "GET" request
	public String getDOHPatients(@RequestParam Optional<String> identifier, 
								 @RequestParam Optional<String> given, 
								 @RequestParam Optional<String> family, 
								 @RequestParam Optional<String> _format) { 
//								 @RequestParam Optional<String> _pretty) {
		
//		System.out.println("[PatientResource] z@RequestParam identifier: " + identifier.isPresent());
//		System.out.println("[PatientResource] z@RequestParam family: " + family.isPresent());
//		System.out.println("[PatientResource] z@RequestParam given: " + given.isPresent());
//		System.out.println("[PatientResource] z@RequestParam _format: " + _format.isPresent());
//		System.out.println("[PatientResource] z@RequestParam _pretty: " + _pretty.isPresent());

		//-------------------------
		// gather and build requested parameter object
		//

		RequestedParameters reqParams = new RequestedParameters();
				
		if (identifier.isPresent()) {
			logger.info("[PatientResource] z@RequestParam identifier.get(): " + identifier.get());
			stateFileNumber = identifier.get().trim();
			if ( stateFileNumber.length() > 0 ) {
				reqParams.setStateFileNumber(stateFileNumber);
			}
		}
		else {
			logger.info("[PatientResource] z@RequestParam identifier.get(): NOT_PRESENT");
		}
		
		if (given.isPresent()) {
			logger.info("[PatientResource] z@RequestParam given.get():  " + given.get());
			deceasedFirst = given.get().trim();  
			if ( deceasedFirst.length() > 0 ) {
				reqParams.setDeceasedFirst(deceasedFirst);
			}
		}

		if (family.isPresent()) {
			logger.info("[PatientResource] z@RequestParam family.get():  " + family.get());
			deceasedLast = family.get().trim();  
			if ( deceasedLast.length() > 0 ) {
				reqParams.setDeceasedLast(deceasedLast);
			}
		}
		
		if (_format.isPresent()) {
			logger.info("[PatientResource] z@RequestParam format.get():  " + _format.get());
			format = _format.get();  
		}else {
			format="json";
		}
		
		
		return patientService.getPatients(reqParams, format);		
	}
	
}
