package com.zak.springboot.death.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.zak.springboot.death.DeathReturnedRow;
import com.zak.springboot.death.RequestedParameters;
import com.zak.springboot.death.configurations.FHIRConfig;

import ca.uhn.fhir.context.FhirContext;

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

	
	public String getPatients(RequestedParameters reqParams, String format) {
		
		// build the sql query
		List<DeathReturnedRow> retRows = new ArrayList<>();

		String query = buildQuery(reqParams);
		logger.info("zBuildQuery(): " + query);
		
		Object[] queryArgsObj = buildQueryArgsArray(reqParams);
		Arrays.stream(queryArgsObj).forEach(x->logger.info("zbuildQueryArgsArray(): " + x.toString()));
		
//List<DeathReturnedRow> retRows
		retRows = mysqlTemplate.query
				  (query,
				   queryArgsObj,
				  new DeathRowMapper()); 
		
		logger.info("zRetRows count: " + retRows.size());		
		retRows.stream().forEach(x->logger.info("z: " + x.toString()) );
		
		
//		return "Dummy return..";
		
		// build fhir bundle
		Bundle bundle = new Bundle();
		
		Patient patient = new Patient();
		
		for (DeathReturnedRow row: retRows) {
			// Create a patient object
			patient = new Patient();
			patient.addIdentifier()
			   .setSystem("http://dev.utah.gov")
			   .setValue(row.getStateFileNumber());
			
			patient.addName()
			   .setFamily(row.getDeceasedLast())
			   .addGiven(row.getDeceasedFirst());
			
			bundle.addEntry().setResource(patient);
		}
				
		// HL7/HAPI/FHIR DSTU3 compliant server
		FhirContext ctx = config.getCtx();
		
		if (format.equalsIgnoreCase("xml")) {
			return ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);	
		}else {
			return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
		}
		
	}


	private Object[] buildQueryArgsArray(RequestedParameters reqParams) {
		// it seems that all columns in the edenmaster table are made of String types
		List<String> argValues = new ArrayList<>();
		if (reqParams.getStateFileNumber() != null && reqParams.getStateFileNumber().length()>0) {
			argValues.add( reqParams.getStateFileNumber() );
		};
		
		if (reqParams.getDeceasedFirst() != null && reqParams.getDeceasedFirst().length()>0) {
			argValues.add( reqParams.getDeceasedFirst() );
		};
		
		if (reqParams.getDeceasedLast() != null && reqParams.getDeceasedLast().length()>0) {
			argValues.add( reqParams.getDeceasedLast() );
		};

		return argValues.toArray();
	}


	private String buildQuery(RequestedParameters reqParams) {	
		
		boolean addAND = false;
		
		String query = "SELECT * FROM edenmaster WHERE ";
		if (reqParams.getStateFileNumber() != null && reqParams.getStateFileNumber().length()>0) {
			query = query + " stateFileNumber = ? ";
			addAND = true;
		};
		
		if (reqParams.getDeceasedFirst() != null && reqParams.getDeceasedFirst().length()>0) {
			query = query + (addAND ? " AND " : "") + " deceasedFirst = ? ";
			addAND = true;
		};
		
		if (reqParams.getDeceasedLast() != null && reqParams.getDeceasedLast().length()>0) {
			query = query + (addAND ? " AND " : "") + " deceasedLast = ? ";
			addAND = true;
		};
				
		return query;
	}
	
// Zak: re-factor when applicable  
//	public String testGetPatients() {
//
//		Bundle bundle = new Bundle();
//		
//		// Create a patient object
//		Patient patient = new Patient();
//		patient.addIdentifier()
//		   .setSystem("???")
////		   .setSystem("http://acme.org/mrns")
//		   .setValue("12345");
//		patient.addName()
//		   .setFamily("Jameson")
//		   .addGiven("J")
//		   .addGiven("Jonah");
//		patient.setGender(AdministrativeGender.MALE);
//
//		bundle.addEntry().setResource(patient);
//		
//		// Create a patient object
//		Patient patient2 = new Patient();
//		patient2.addIdentifier()
//		   .setSystem("???")
////		   .setSystem("http://acme.org/mrns")
//		   .setValue("56789");
//		patient2.addName()
//		   .setFamily("Morris6")
//		   .addGiven("F")
//		   .addGiven("Frank");
//		patient2.setGender(AdministrativeGender.MALE);
//
//		bundle.addEntry().setResource(patient2);
//		
//		// connecting to a DSTU3 compliant server
//		FhirContext ctx = config.getCtx();
//		
//		return ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
////		return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
//	}
	
	
	class DeathRowMapper implements RowMapper<DeathReturnedRow>
	{
		@Override
		public DeathReturnedRow mapRow(ResultSet rs, int rowNum) throws SQLException {
			DeathReturnedRow deathRetRow = new DeathReturnedRow();
			deathRetRow.setStateFileNumber(rs.getString("stateFileNumber"));
			deathRetRow.setDeceasedFirst(rs.getString("deceasedFirst"));
			deathRetRow.setDeceasedLast(rs.getString("deceasedLast"));
			return deathRetRow;
		}			
	}		

}
