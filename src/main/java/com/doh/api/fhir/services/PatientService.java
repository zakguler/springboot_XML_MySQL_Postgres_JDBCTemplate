package com.doh.api.fhir.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Address.AddressUse;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleLinkComponent;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Identifier.IdentifierUse;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.doh.api.fhir.DeathReturnedRow;
import com.doh.api.fhir.RequestedParameters;
import com.doh.api.fhir.configurations.FHIRConfig;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;

@Service	//<================= business services are singleton
public class PatientService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	FHIRConfig config;

	@Autowired
	@Qualifier("postgresJdbcTemplate")
	private JdbcTemplate postgresTemplate;

	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate mysqlTemplate;
	
	/*
	 * Note:
	 * 		When adding a new user request parameter:
	 * 			-[PatientController::getDOHPatients()] [@GetMapping("/Patient")] api. add Optional<[new user parameter]> to the Get Mapping() method.
	 * 			-[RequestedParameters] add the new field and its value to the 'RequestedParameters' object.
	 * 			-[buildQuery(reqParams)] add it to the generated [SELECT/WHERE 'column names'][?] sql query statement.
	 * 			-[buildQueryArgsArray(reqParams)] extract the columns values from the 'RequestedParameters' object
	 * 											  into a list which will be used in the JDBCTemplate query call.
	 * 			-[DeathReturnedRow] contains all columns needed for the response object.
	 * 			-[DeathRowMapper] populate the DeathReturnedRow object from the returned query's result set.
	 * 			-[generateBundle(retRows)] build the [fhir bundle/patient] object.
	 * 			
	 */
	public String getPatients(RequestedParameters reqParams, String uri, String format) {
		
		/*
		 * build the sql query
		 */
		
		List<DeathReturnedRow> deathReturnedRows = new ArrayList<>();

		// generate "SELECT/WHERE' sql statement.
		String query = buildQuery(reqParams);
		logger.info("zBuildQuery(): " + query);
		
		Object[] queryArgsObj = buildQueryArgsArray(reqParams);
		Arrays.stream(queryArgsObj).forEach(x->logger.info("zbuildQueryArgsArray(): " + x.toString()));
		
		//List<DeathReturnedRow> retRows
		deathReturnedRows = mysqlTemplate.query
				  (query,
				   queryArgsObj,
				   new DeathRowMapper()); 
		
		logger.info("zRetRows count: " + deathReturnedRows.size());		
		deathReturnedRows.stream().forEach(x->logger.info("z: " + x.toString()) );
				
		// build fhir bundle
		Bundle bundle = generateBundle(deathReturnedRows, uri);
				
		// HL7/HAPI/FHIR DSTU3 compliant server
		FhirContext ctx = config.getCtx();
		
		String outFhirString = ""; 
		if (format.equalsIgnoreCase("xml")) {
			outFhirString = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);	
		}else {
			outFhirString = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
		}

		return outFhirString;
		
	}


	private Bundle generateBundle(List<DeathReturnedRow> retRows, String uri) {
		Bundle bundle = new Bundle();
		BundleLinkComponent bundleLinkComp = new BundleLinkComponent();
		bundleLinkComp.setUrl(uri);	//get it from the request header, EX: http://localhost:5151/fhir/Patient?given=Ruth&_format=json
		List<BundleLinkComponent> blComponents = new ArrayList<>();
		blComponents.add(bundleLinkComp);
		
		bundle.setType(Bundle.BundleType.DOCUMENT)// BundleType???
				.setTotal(retRows.size())
				.setLink(blComponents);		
		
		
		Patient patient = new Patient();
		
		for (DeathReturnedRow row: retRows) {
			// Create a patient object
			patient = new Patient();
			
			// identifier: use, system, value 
			patient.addIdentifier()
				.setUse(IdentifierUse.OFFICIAL) //"official"
			    .setSystem("Eden Identifier")
			    .setValue(row.getStateFileNumber());
			
			// name: family, given
			patient.addName()
			   .setFamily(row.getDeceasedLast())
			   .addGiven(row.getDeceasedFirst());
			
			
			// birthdate
			Date bDate = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); // "20-10-2013"
			String bDateInString = (	row.getBirthDD()
									+ "-" + ( row.getBirthMM())
									+ "-" + row.getBirthCCYY());
			logger.info("zBirthDateInString: " + bDateInString);
	        try {
	        	bDate = formatter.parse(bDateInString);
	        }catch(Exception e) {
	        	logger.error("PatientService: failed date conversion...");
	        }	        
	        patient.setBirthDate(bDate);
	        
	        
	        // gender
	        String gender = row.getGender();
	        switch(gender) {
		        case "M":
		        case "m":
		        	patient.setGender(AdministrativeGender.MALE);
		        	break;
		        case "F":
		        case "f":
		        	patient.setGender(AdministrativeGender.FEMALE);
		        	break;
		        case "O":
		        case "o":
		        	patient.setGender(AdministrativeGender.OTHER);
		        	break;
		        default:
		        patient.setGender(AdministrativeGender.UNKNOWN);
	        }
	        
	        // address
	        StringType resStreetAdrress1 = new StringType(row.getResStreetAdrress1());
//	        StringType resStreetAdrress2 = new StringType(row.getResStreetAdrress2()); // ignore it for now. could be an apt#
	        List<StringType> line = new ArrayList<>();
	        line.add(resStreetAdrress1);
	        
	        Address addr = new Address();
	        addr.setUse(AddressUse.HOME) // [home|work|temp|old]
	        	.setLine(line)
		        .setCity(row.getResCity())
		        .setDistrict(row.getResCounty())
		        .setState(row.getResState())
		        .setPostalCode(row.getResZip5())
		        .setCountry(row.getResCountry());
	        patient.addAddress(addr);
	        
			
	        // deceased: date and time
			Date deceased = new Date();
			formatter = new SimpleDateFormat("dd-MM-yyyy-hh.mm.ss"); // "20-10-2013"
			String deceasedDateInString = (	row.getDeathDD()
									+ "-" + row.getDeathMM()
									+ "-" + row.getDeathCCYY()
									+ "-" + row.getTimeOfDeathHH()
									+ "." + row.getTimeOfDeathMM()
									+ "." + "00");

			logger.info("zDeceasedDateInString: " + deceasedDateInString);
	        try {
	        	deceased = formatter.parse(deceasedDateInString);
	        }catch(Exception e) {
	        	logger.error("PatientService: failed date conversion...");
	        }	 
	        Type deceased2 = new DateTimeType(deceased);
	        patient.setDeceased(deceased2);
	        
	        
	        
	        //-----
	        // temporary UUID used here for convenience only
	        //
	        // Give the patient a temporary UUID so that other resources in
	        // the transaction can refer to it
	        patient.setId(IdDt.newRandomUuid());
	        
			//-----
			// create a bundle entry: Person resource with the temporary UUID
			bundle.addEntry()
				.setResource(patient)
				.setFullUrl(patient.getId());

			//-----
			// Create observation object(s)
			//
			Observation observation = new Observation();
			CodeableConcept category = new CodeableConcept();
			StringType stringType = new StringType();

	        //for (Observation observation: observationList) {
				observation = new Observation();
			
		        // The observation refers to the patient using the ID, which is already
		        // set to a temporary UUID 
		        observation.setSubject(new Reference(patient.getId()));
		        
				observation.setStatus(Observation.ObservationStatus.FINAL)
							.setId("Cause-of-death-coded");				
	
				//-----
				// add observation category object
				category = new CodeableConcept();
				category.addCoding()
							.setSystem("http://hl7.org/fhir/observation-category")
							.setCode("cause-death-coded")
							.setDisplay("Cause Death Coded");
				category.setText("Cause of Death Coded");
				
				observation.addCategory(category);
				
				//-----
				// add list of component(s) to the observation object(s)
				//
				// it seems that you can ONLY add one [component..value] to the observation-component,
				// in this our case, 'CodeableConcept' or 'StringType'
				//
				ObservationComponentComponent component = new ObservationComponentComponent();
				CodeableConcept codeableConcept = new CodeableConcept(); 
				Coding coding = new Coding();
				Coding codingValue = new Coding();
				CodeableConcept codeableConceptValue = new CodeableConcept();
				List<Coding> codingsValue = new ArrayList<>();
				List<Coding> codings = new ArrayList<>();
				
		        //for (Observation observation: observationList) {
				
					// edenmaster..UnderlyingCode
					if (row.getUnderlyingCode() != null && row.getUnderlyingCode().trim().length() > 0) {
						component = new ObservationComponentComponent();
							codeableConcept = new CodeableConcept(); 
								coding = new Coding();		
	//							coding.setCode("80359-5")
								coding.setCode(row.getUnderlyingCode())
									.setSystem("http://loinc.org")					
									.setDisplay("Underlying Code");
								codings = new ArrayList<>();
								codings.add(coding);					
							codeableConcept.setCoding(codings);				
						component.setCode(codeableConcept);	
							
							codeableConceptValue = new CodeableConcept(); 
								codingValue = new Coding();		
								codingValue.setCode("X42")	// ???
									.setSystem("http://hl7.org/fhir/sid/icd-10") // ???					
									.setDisplay("Accidental poisoning by and exposure to other and unspecified drugs, medicaments and biological substances"); // ???
								codingsValue = new ArrayList<>();
								codingsValue.add(codingValue);					
							codeableConceptValue.setCoding(codingsValue);
						component.setValue(codeableConceptValue);
							
						observation.addComponent(component);
					}
	

					//----
					// edenmaster..ContribCode1
					if (row.getContribCode1() != null && row.getContribCode1().trim().length() > 0) {						
						component = new ObservationComponentComponent();
							codeableConcept = new CodeableConcept(); 
								coding = new Coding();		
//								coding.setCode("80357-7")
								coding.setCode(row.getContribCode1())
									.setSystem("http://loinc.org") // ???					
									.setDisplay("ContribCode1");
								codings = new ArrayList<>();
								codings.add(coding);					
							codeableConcept.setCoding(codings);				
						component.setCode(codeableConcept);	
							
							codeableConceptValue = new CodeableConcept(); 
								codingValue = new Coding();		
								codingValue.setCode("T402") // ???
									.setSystem("http://hl7.org/fhir/sid/icd-10") // ???					
									.setDisplay("Accidental poisoning by and exposure to other and unspecified drugs, medicaments and biological substances"); // ???
								codingsValue = new ArrayList<>();
								codingsValue.add(codingValue);					
							codeableConceptValue.setCoding(codingsValue);
						component.setValue(codeableConceptValue);
							
						observation.addComponent(component);
					}


					//----
					// edenmaster..ContribCode2
					if (row.getContribCode2() != null && row.getContribCode2().trim().length() > 0) {						
						component = new ObservationComponentComponent();
							codeableConcept = new CodeableConcept(); 
								coding = new Coding();		
//								coding.setCode("80357-7")
								coding.setCode(row.getContribCode2())
									.setSystem("http://loinc.org") // ???					
									.setDisplay("ContribCode2");
								codings = new ArrayList<>();
								codings.add(coding);					
							codeableConcept.setCoding(codings);				
						component.setCode(codeableConcept);	
							
							codeableConceptValue = new CodeableConcept(); 
								codingValue = new Coding();		
								codingValue.setCode("T402") // ???
									.setSystem("http://hl7.org/fhir/sid/icd-10") // ???					
									.setDisplay("Accidental poisoning by and exposure to other and unspecified drugs, medicaments and biological substances"); // ???
								codingsValue = new ArrayList<>();
								codingsValue.add(codingValue);					
							codeableConceptValue.setCoding(codingsValue);
						component.setValue(codeableConceptValue);
							
						observation.addComponent(component);
					}

					//----
					// edenmaster..ContribCode3
					if (row.getContribCode3() != null && row.getContribCode3().trim().length() > 0) {						
						component = new ObservationComponentComponent();
							codeableConcept = new CodeableConcept(); 
								coding = new Coding();		
//								coding.setCode("80357-7")
								coding.setCode(row.getContribCode3())
									.setSystem("http://loinc.org") // ???					
									.setDisplay("ContribCode3");
								codings = new ArrayList<>();
								codings.add(coding);					
							codeableConcept.setCoding(codings);				
						component.setCode(codeableConcept);	
							
							codeableConceptValue = new CodeableConcept(); 
								codingValue = new Coding();		
								codingValue.setCode("T402") // ???
									.setSystem("http://hl7.org/fhir/sid/icd-10") // ???					
									.setDisplay("Accidental poisoning by and exposure to other and unspecified drugs, medicaments and biological substances"); // ???
								codingsValue = new ArrayList<>();
								codingsValue.add(codingValue);					
							codeableConceptValue.setCoding(codingsValue);
						component.setValue(codeableConceptValue);
							
						observation.addComponent(component);
					}


					//----
					// edenmaster..ContribCode4
					if (row.getContribCode4() != null && row.getContribCode4().trim().length() > 0) {						
						component = new ObservationComponentComponent();
							codeableConcept = new CodeableConcept(); 
								coding = new Coding();		
//								coding.setCode("80357-7")
								coding.setCode(row.getContribCode4())
									.setSystem("http://loinc.org") // ???					
									.setDisplay("ContribCode4");
								codings = new ArrayList<>();
								codings.add(coding);					
							codeableConcept.setCoding(codings);				
						component.setCode(codeableConcept);	
							
							codeableConceptValue = new CodeableConcept(); 
								codingValue = new Coding();		
								codingValue.setCode("T402") // ???
									.setSystem("http://hl7.org/fhir/sid/icd-10") // ???					
									.setDisplay("Accidental poisoning by and exposure to other and unspecified drugs, medicaments and biological substances"); // ???
								codingsValue = new ArrayList<>();
								codingsValue.add(codingValue);					
							codeableConceptValue.setCoding(codingsValue);
						component.setValue(codeableConceptValue);
							
						observation.addComponent(component);
					}


					//----
					// edenmaster..ContribCode5
					if (row.getContribCode5() != null && row.getContribCode5().trim().length() > 0) {						
						component = new ObservationComponentComponent();
							codeableConcept = new CodeableConcept(); 
								coding = new Coding();		
//								coding.setCode("80357-7")
								coding.setCode(row.getContribCode5())
									.setSystem("http://loinc.org") // ???					
									.setDisplay("ContribCode5");
								codings = new ArrayList<>();
								codings.add(coding);					
							codeableConcept.setCoding(codings);				
						component.setCode(codeableConcept);	
							
							codeableConceptValue = new CodeableConcept(); 
								codingValue = new Coding();		
								codingValue.setCode("T402") // ???
									.setSystem("http://hl7.org/fhir/sid/icd-10") // ???					
									.setDisplay("Accidental poisoning by and exposure to other and unspecified drugs, medicaments and biological substances"); // ???
								codingsValue = new ArrayList<>();
								codingsValue.add(codingValue);					
							codeableConceptValue.setCoding(codingsValue);
						component.setValue(codeableConceptValue);
							
						observation.addComponent(component);
					}


					//----
					// edenmaster..ContribCode6
					if (row.getContribCode6() != null && row.getContribCode6().trim().length() > 0) {						
						component = new ObservationComponentComponent();
							codeableConcept = new CodeableConcept(); 
								coding = new Coding();		
//								coding.setCode("80357-7")
								coding.setCode(row.getContribCode6())
									.setSystem("http://loinc.org") // ???					
									.setDisplay("ContribCode6");
								codings = new ArrayList<>();
								codings.add(coding);					
							codeableConcept.setCoding(codings);				
						component.setCode(codeableConcept);	
							
							codeableConceptValue = new CodeableConcept(); 
								codingValue = new Coding();		
								codingValue.setCode("T402") // ???
									.setSystem("http://hl7.org/fhir/sid/icd-10") // ???					
									.setDisplay("Accidental poisoning by and exposure to other and unspecified drugs, medicaments and biological substances"); // ???
								codingsValue = new ArrayList<>();
								codingsValue.add(codingValue);					
							codeableConceptValue.setCoding(codingsValue);
						component.setValue(codeableConceptValue);
							
						observation.addComponent(component);
					}


					//----
					// edenmaster..ContribCode7
					if (row.getContribCode7() != null && row.getContribCode7().trim().length() > 0) {						
						component = new ObservationComponentComponent();
							codeableConcept = new CodeableConcept(); 
								coding = new Coding();		
//								coding.setCode("80357-7")
								coding.setCode(row.getContribCode7())
									.setSystem("http://loinc.org") // ???					
									.setDisplay("ContribCode7");
								codings = new ArrayList<>();
								codings.add(coding);					
							codeableConcept.setCoding(codings);				
						component.setCode(codeableConcept);	
							
							codeableConceptValue = new CodeableConcept(); 
								codingValue = new Coding();		
								codingValue.setCode("T402") // ???
									.setSystem("http://hl7.org/fhir/sid/icd-10") // ???					
									.setDisplay("Accidental poisoning by and exposure to other and unspecified drugs, medicaments and biological substances"); // ???
								codingsValue = new ArrayList<>();
								codingsValue.add(codingValue);					
							codeableConceptValue.setCoding(codingsValue);
						component.setValue(codeableConceptValue);
							
						observation.addComponent(component);
					}


					//----
					// edenmaster..ContribCode8
					if (row.getContribCode8() != null && row.getContribCode8().trim().length() > 0) {						
						component = new ObservationComponentComponent();
							codeableConcept = new CodeableConcept(); 
								coding = new Coding();		
//								coding.setCode("80357-7")
								coding.setCode(row.getContribCode8())
									.setSystem("http://loinc.org") // ???					
									.setDisplay("ContribCode8");
								codings = new ArrayList<>();
								codings.add(coding);					
							codeableConcept.setCoding(codings);				
						component.setCode(codeableConcept);	
							
							codeableConceptValue = new CodeableConcept(); 
								codingValue = new Coding();		
								codingValue.setCode("T402") // ???
									.setSystem("http://hl7.org/fhir/sid/icd-10") // ???					
									.setDisplay("Accidental poisoning by and exposure to other and unspecified drugs, medicaments and biological substances"); // ???
								codingsValue = new ArrayList<>();
								codingsValue.add(codingValue);					
							codeableConceptValue.setCoding(codingsValue);
						component.setValue(codeableConceptValue);
							
						observation.addComponent(component);
					}


					//----
					// edenmaster..ContribCode9
					if (row.getContribCode9() != null && row.getContribCode9().trim().length() > 0) {						
						component = new ObservationComponentComponent();
							codeableConcept = new CodeableConcept(); 
								coding = new Coding();		
//								coding.setCode("80357-7")
								coding.setCode(row.getContribCode9())
									.setSystem("http://loinc.org") // ???					
									.setDisplay("ContribCode9");
								codings = new ArrayList<>();
								codings.add(coding);					
							codeableConcept.setCoding(codings);				
						component.setCode(codeableConcept);	
							
							codeableConceptValue = new CodeableConcept(); 
								codingValue = new Coding();		
								codingValue.setCode("T402") // ???
									.setSystem("http://hl7.org/fhir/sid/icd-10") // ???					
									.setDisplay("Accidental poisoning by and exposure to other and unspecified drugs, medicaments and biological substances"); // ???
								codingsValue = new ArrayList<>();
								codingsValue.add(codingValue);					
							codeableConceptValue.setCoding(codingsValue);
						component.setValue(codeableConceptValue);
							
						observation.addComponent(component);
					}


					
				//} // end of componentList loop

				//----
				// Add the first observation1 
				bundle.addEntry()
				.setResource(observation)
				.getRequest()
				.setUrl("Observation")
				.setMethod(HTTPVerb.GET);
				
				
				//----
				// Add the second observation
		        observation = new Observation();
		        // The observation refers to the patient using the ID, which is already
		        // set to a temporary UUID 
		        observation.setSubject(new Reference(patient.getId()));
		        
				observation.setStatus(Observation.ObservationStatus.FINAL)
							.setId("Cause-of-death-text");				

				//-----
				// add observation category object
				category = new CodeableConcept();
				category.addCoding()
							.setSystem("http://hl7.org/fhir/observation-category")
							.setCode("cause-death-text")
							.setDisplay("Cause Death Text");
				category.setText("Cause of Death Text");
				
				observation.addCategory(category);

				
				//----
				// edenmaster..ImmediateCause
				if (row.getImmediateCause() != null && row.getImmediateCause().trim().length() > 0) {
					component = new ObservationComponentComponent();
					
						codeableConcept = new CodeableConcept(); 
							coding = new Coding();		
//							coding.setCode("ImmediateCause")
							coding.setCode(row.getImmediateCause())
								.setSystem("EDEN")	// ???				
								.setDisplay("Immediate Cause");
							codings = new ArrayList<>();
							codings.add(coding);					
						codeableConcept.setCoding(codings);				
					component.setCode(codeableConcept);	
						
						stringType = new StringType();
						stringType.setValue("Blunt force injuries"); // ???
					component.setValue(stringType);					
						
					observation.addComponent(component);
				}
				
				//----
				// edenmaster..AdditionalCause1
				if (row.getAdditionalCause1() != null && row.getAdditionalCause1().trim().length() > 0) {
					component = new ObservationComponentComponent();
					
						codeableConcept = new CodeableConcept(); 
							coding = new Coding();		
							coding.setCode(row.getAdditionalCause1())
								.setSystem("EDEN")	// ???			
								.setDisplay("Additional Cause 1");
							codings = new ArrayList<>();
							codings.add(coding);					
						codeableConcept.setCoding(codings);				
					component.setCode(codeableConcept);	
						
						stringType = new StringType();
						stringType.setValue("Blunt force injuries"); // ???
					component.setValue(stringType);					
						
					observation.addComponent(component);				
				}

				
				//----
				// edenmaster..AdditionalCause2
				if (row.getAdditionalCause2() != null && row.getAdditionalCause2().trim().length() > 0) {
					component = new ObservationComponentComponent();
					
						codeableConcept = new CodeableConcept(); 
							coding = new Coding();		
							coding.setCode(row.getAdditionalCause2())
								.setSystem("EDEN")	// ???				
								.setDisplay("Additional Cause 2");
							codings = new ArrayList<>();
							codings.add(coding);					
						codeableConcept.setCoding(codings);				
					component.setCode(codeableConcept);	
						
						stringType = new StringType();
						stringType.setValue("Blunt force injuries"); // ???
					component.setValue(stringType);					
						
					observation.addComponent(component);				
				}

				
				//----
				// edenmaster..OtherConditions
				if (row.getOtherConditions() != null && row.getOtherConditions().trim().length() > 0) {
				component = new ObservationComponentComponent();
				
					codeableConcept = new CodeableConcept(); 
						coding = new Coding();		
						coding.setCode(row.getOtherConditions())
							.setSystem("EDEN") // ???			
							.setDisplay("Other Conditions");
						codings = new ArrayList<>();
						codings.add(coding);					
					codeableConcept.setCoding(codings);				
				component.setCode(codeableConcept);	
					
					stringType = new StringType();
					stringType.setValue("Citalopram toxicity"); // ???
				component.setValue(stringType);					
					
				observation.addComponent(component);				
				}
				
				
				//----
				// edenmaster..UnderlyingCause
				if (row.getUnderlyingCause() != null && row.getUnderlyingCause().trim().length() > 0) {
				component = new ObservationComponentComponent();
				
					codeableConcept = new CodeableConcept(); 
						coding = new Coding();		
						coding.setCode(row.getUnderlyingCause())
							.setSystem("EDEN")	// ???			
							.setDisplay("Underlying Cause");
						codings = new ArrayList<>();
						codings.add(coding);					
					codeableConcept.setCoding(codings);				
				component.setCode(codeableConcept);	
					
					stringType = new StringType();
					stringType.setValue("Blunt force injuries"); // ???
				component.setValue(stringType);					
					
				observation.addComponent(component);				
				}
				
				
				//----
				// Add the second observation 
				bundle.addEntry()
				.setResource(observation)
				.getRequest()
				.setUrl("Observation")
				.setMethod(HTTPVerb.GET);

				
			//} // end of observationList loop
			
		} // end of patientList [DeathReturnedRow's] loop
		
		
		
		      
		
		
		return bundle;
	}


	/*
	 * build the object that contains user's requested parameters 
	 * [to be used in the sql's WHERE clause]
	 */
	private Object[] buildQueryArgsArray(RequestedParameters reqParams) {
		
		/*
		 * NOTE:
		 * 		[for now] the order of the elements are very important. do not sort the list
		 * 		[to do] use a Map instead of List to store the parameters.
		 */
		
		// it seems that all columns in the edenmaster table are made of String types
		
		// [todo] change the ArrayList<String> to a Map<String key, String value>
		List<String> argValues = new ArrayList<>();
		
		if (reqParams.getReq_stateFileNumber() != null && reqParams.getReq_stateFileNumber().length()>0) {
			argValues.add( reqParams.getReq_stateFileNumber() );
		};
		
		if (reqParams.getReq_deceasedFirst() != null && reqParams.getReq_deceasedFirst().length()>0) {
			argValues.add( reqParams.getReq_deceasedFirst() );
		};
		
		if (reqParams.getReq_deceasedLast() != null && reqParams.getReq_deceasedLast().length()>0) {
			argValues.add( reqParams.getReq_deceasedLast() );
		};

		// todo:
		// use ccyy, mm, dd
		if (reqParams.getReq_birthdate() != null && reqParams.getReq_birthdate().length()>0) {
			// when we get to this point, it is a valid birthdate			
			String bDate =  reqParams.getReq_birthdate();
			String[] bDateArray = bDate.split("-");	// date format is [yyyy-mm-dd]
			argValues.add(bDateArray[0]);
			argValues.add(bDateArray[1]);
			argValues.add(bDateArray[2]);
		};

		return argValues.toArray();
	}

	/*
	 * build the sql's WHERE clause 
	 */
	private String buildQuery(RequestedParameters reqParams) {	

		/*
		 * NOTE:
		 * 		[for now] the order of the elements in the WHERE clause is very important.
		 * if you need to change it, please sync it with the [ List<String> argValues ]
		 */

		boolean addAND = false;
		
		String query = "SELECT * FROM edenmaster WHERE ";
		if (reqParams.getReq_stateFileNumber() != null && reqParams.getReq_stateFileNumber().length()>0) {
			query = query + " stateFileNumber = ? ";
			addAND = true;
		};
		
		if (reqParams.getReq_deceasedFirst() != null && reqParams.getReq_deceasedFirst().length()>0) {
			query = query + (addAND ? " AND " : "") + " deceasedFirst = ? ";
			addAND = true;
		};
		
		if (reqParams.getReq_deceasedLast() != null && reqParams.getReq_deceasedLast().length()>0) {
			query = query + (addAND ? " AND " : "") + " deceasedLast = ? ";
			addAND = true;
		};
	
		if (reqParams.getReq_birthdate()!= null && reqParams.getReq_birthdate().length()>0) {
			query = query + (addAND ? " AND " : "") + " birthCCYY = ? ";
			addAND = true;
			query = query + (addAND ? " AND " : "") + " birthMM = ? ";
			query = query + (addAND ? " AND " : "") + " birthDD = ? ";			
		};
				
		return query;
	}
	
	
	/*
	 * this is a list of all columns needed from the edenmaster table
	 * populate the DeathReturnedRow object from the sql's result set
	 */
	class DeathRowMapper implements RowMapper<DeathReturnedRow>
	{
		@Override
		public DeathReturnedRow mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			DeathReturnedRow deathRetRow = new DeathReturnedRow();
			deathRetRow.setStateFileNumber(rs.getString("stateFileNumber"));
			deathRetRow.setDeceasedFirst(rs.getString("deceasedFirst"));
			deathRetRow.setDeceasedLast(rs.getString("deceasedLast"));

			deathRetRow.setBirthCCYY(rs.getString("BirthCCYY"));
			deathRetRow.setBirthMM(rs.getString("BirthMM"));
			deathRetRow.setBirthDD(rs.getString("BirthDD"));

			deathRetRow.setGender(rs.getString("gender"));

			deathRetRow.setResStreetAdrress1(rs.getString("ResStreetAddress1"));
//			deathRetRow.setResStreetAdrress2(rs.getString("ResStreetAddress2"));
			deathRetRow.setResCity(rs.getString("resCity"));
			deathRetRow.setResCounty(rs.getString("resCounty"));
			deathRetRow.setResState(rs.getString("resState"));
			deathRetRow.setResZip5(rs.getString("resZip5"));
			deathRetRow.setResCountry(rs.getString("resCountry"));

			deathRetRow.setDeathCCYY(rs.getString("deathCCYY"));
			deathRetRow.setDeathMM(rs.getString("deathMM"));
			deathRetRow.setDeathDD(rs.getString("deathDD"));
			
			deathRetRow.setTimeOfDeathHH(rs.getString("TimeOfDeathHH"));
			deathRetRow.setTimeOfDeathMM(rs.getString("TimeOfDeathMM"));
			// add TimeOfDeathHH/MM
			
			deathRetRow.setUnderlyingCode(rs.getString("UnderlyingCode"));
			deathRetRow.setContribCode1(rs.getString("ContribCode1"));
			deathRetRow.setContribCode2(rs.getString("ContribCode2"));
			deathRetRow.setContribCode3(rs.getString("ContribCode3"));
			deathRetRow.setContribCode4(rs.getString("ContribCode4"));
			deathRetRow.setContribCode5(rs.getString("ContribCode5"));
			deathRetRow.setContribCode6(rs.getString("ContribCode6"));
			deathRetRow.setContribCode7(rs.getString("ContribCode7"));
			deathRetRow.setContribCode8(rs.getString("ContribCode8"));
			deathRetRow.setContribCode9(rs.getString("ContribCode9"));

			deathRetRow.setImmediateCause(rs.getString("ImmediateCause"));
			deathRetRow.setAdditionalCause1(rs.getString("AdditionalCause1"));
			deathRetRow.setAdditionalCause2(rs.getString("AdditionalCause2"));
			deathRetRow.setOtherConditions(rs.getString("OtherConditions"));
			deathRetRow.setUnderlyingCause(rs.getString("UnderlyingCause"));

			return deathRetRow;
		}			
	}		

}

