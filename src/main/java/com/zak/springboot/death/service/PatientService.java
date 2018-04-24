package com.zak.springboot.death.service;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zak.springboot.death.config.FHIRConfig;

import ca.uhn.fhir.context.FhirContext;

@Service	//<================= business services are singleton
public class PatientService {
	
	
	@Autowired
	FHIRConfig config;

	
	// Zak: re-factor when applicable  
	public String getPatients() {

		Bundle bundle = new Bundle();
		
		// Create a patient object
		Patient patient = new Patient();
		patient.addIdentifier()
		   .setSystem("???")
//		   .setSystem("http://acme.org/mrns")
		   .setValue("12345");
		patient.addName()
		   .setFamily("Jameson")
		   .addGiven("J")
		   .addGiven("Jonah");
		patient.setGender(AdministrativeGender.MALE);

		bundle.addEntry().setResource(patient);
		
		// Create a patient object
		Patient patient2 = new Patient();
		patient2.addIdentifier()
		   .setSystem("???")
//		   .setSystem("http://acme.org/mrns")
		   .setValue("56789");
		patient2.addName()
		   .setFamily("Morris6")
		   .addGiven("F")
		   .addGiven("Frank");
		patient2.setGender(AdministrativeGender.MALE);

		bundle.addEntry().setResource(patient2);
		
		// connecting to a DSTU3 compliant server
		FhirContext ctx = config.getCtx();
		
		return ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
//		return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
	}
}
