package com.zak.springboot.death.service;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.client.api.IGenericClient;

@Service	//<================= business services are singleton
public class Junk__PatientService {
	
	// Zak: re-factor when applicable  
	public Bundle getAllPatients() {
		// TODO Auto-generated method stub
		// We're connecting to a DSTU1 compliant server in this example
		FhirContext ctx = FhirContext.forDstu3();
		String serverBase = "http://fhirtest.uhn.ca/baseDstu3";
		 
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);
		 
		// Perform a search
		Bundle results = client
		      .search()
		      .forResource(Patient.class)
		      .where(Patient.FAMILY.matches().value("duck"))
		      .returnBundle(Bundle.class)
		      .execute();
		 
		System.out.println("Found " + results.getEntry().size() + " patients named 'duck'");
		return results;
	}

	
	
	public String getbundle() {
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
//z		patient.setGender(AdministrativeGenderEnum.MALE);
		 
		// Give the patient a temporary UUID so that other resources in
		// the transaction can refer to it
		patient.setId(IdDt.newRandomUuid());

//		// Log the request
		FhirContext ctx = FhirContext.forDstu3();
//		// Create a client and post the transaction to the server
		IGenericClient client = ctx.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu3");
//		IGenericClient client = ctx.newRestfulGenericClient("http://localhost:3000");
		// Perform a search
		
//		ca.uhn.fhir.model.dstu3.resource.Bundle response = client.search()
		Bundle response = client.search()
				  .forResource(Patient.class)
				  .where(Patient.FAMILY.matches().values("Morris6", "Smith"))
				  .returnBundle(Bundle.class)			  
				  .count(6)				  
				  .execute();
		
		return ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(response);
		
//		IQuery<Bundle> response = client.search()
//				  .forResource(Patient.class)
//				  .where(Patient.FAMILY.matches().values("Morris6", "Smith"))
//				  .returnBundle(Bundle.class);			  

		  

//		Bundle results = client
//		      .search()
//		      .forResource(Patient.class)
//		      .where(Patient.FAMILY.matches().value("duck"))
//		      .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
//		      .execute();
		
//		return null;
//		return response;
//		return bundle;
//		return results;

	}
	
	
	
	// http://hapifhir.io/doc_rest_client_examples.html#Fetch_all_Pages_of_a_Bundle
	// Zak: testing this rest api
	public Patient addPatient() {
		// Create a patient object
		Patient patient = new Patient();
		patient.addIdentifier()
		   .setSystem("http://acme.org/mrns")
		   .setValue("12345");
		patient.addName()
		   .setFamily("Jameson")
		   .addGiven("J")
		   .addGiven("Jonah");
		patient.setGender(AdministrativeGender.MALE);
//z		patient.setGender(AdministrativeGenderEnum.MALE);
		 
		// Give the patient a temporary UUID so that other resources in
		// the transaction can refer to it
		patient.setId(IdDt.newRandomUuid());
		 
		// Create an observation object
		Observation observation = new Observation();
		observation.setStatus(ObservationStatus.FINAL);
//z		observation.setStatus(ObservationStatusEnum.FINAL);
		observation
		   .getCode()
		      .addCoding()
		         .setSystem("http://loinc.org")
		         .setCode("789-8")
		         .setDisplay("Erythrocytes [#/volume] in Blood by Automated count");
		observation.setValue(
		   new Quantity()
		      .setValue(4.12)
		      .setUnit("10 trillion/L")
		      .setSystem("http://unitsofmeasure.org")
		      .setCode("10*12/L"));
		 
		// The observation refers to the patient using the ID, which is already
		// set to a temporary UUID 
		observation.setSubject(new Reference(patient.getId().toString()));
//z		observation.setSubject(new ResourceReferenceDt(patient.getId().getValue()));
		 
		// Create a bundle that will be used as a transaction
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.TRANSACTION);
//z		bundle.setType(BundleTypeEnum.TRANSACTION);
		 
		// Add the patient as an entry. This entry is a POST with an
		// If-None-Exist header (conditional create) meaning that it
		// will only be created if there isn't already a Patient with
		// the identifier 12345
		bundle.addEntry()
		   .setFullUrl(patient.getId().toString())
//z		   .setFullUrl(patient.getId().getValue())
		   .setResource(patient)
		   .getRequest()
		      .setUrl("Patient")
		      .setIfNoneExist("identifier=http://acme.org/mrns|12345")
		      .setMethod(HTTPVerb.POST);
//z		      .setMethod(HTTPVerbEnum.POST);
		 
		// Add the observation. This entry is a POST with no header
		// (normal create) meaning that it will be created even if
		// a similar resource already exists.
		bundle.addEntry()
		   .setResource(observation)
		   .getRequest()
		      .setUrl("Observation")
		      .setMethod(HTTPVerb.POST);
//z			  .setMethod(HTTPVerbEnum.POST);

		//
		// Note:
		//
		// that FhirContext is an expensive object to create, 
		// so you should try to keep an instance around for the lifetime of your application.
		// It is thread-safe so it can be passed as needed.
		//
		// Client instances, on the other hand, are very inexpensive to create 
		// so you can create a new one for each request if needed 
		// (although there is no requirement to do so, clients are reusable and thread-safe as well).
		//
		
//		// Log the request
		FhirContext ctx = FhirContext.forDstu3();
		System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle));
		
//		// Create a client and post the transaction to the server
		IGenericClient client = ctx.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu3");
		
//		Bundle resp = client.transaction().withBundle(bundle).execute();
//		 
//		// Log the response
//???		System.out.println(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resp));	
		
		
//		// Invoke the client
		// Perform a search
		Bundle results = client
		      .search()
		      .forResource(Patient.class)
		      .where(Patient.FAMILY.matches().value("Jameson"))
		      .returnBundle(Bundle.class)
		      .execute();
		 
		System.out.println("Found " + results.getEntry().size() + " patients named 'Jonah'");
//		bundle = client.search().forResource(Patient.class)
//		.where(new StringClientParam("given").matches().value("Frank"))
//		.where(new StringClientParam("family").matches().value("Morris6"))
//		.prettyPrint()
////		.limitTo(5)
//		.execute();
		
		return patient;
		
	}
	
	

}
