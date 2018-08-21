package com.doh.api.fhir.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doh.api.exception_resources.CustomBadRequestException;
import com.doh.api.exception_resources.CustomNotFoundException;
import com.doh.api.fhir.RequestedParameters;
import com.doh.api.fhir.configurations.FHIRConfig;
import com.doh.api.fhir.services.PatientService;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.ResourceVersionConflictException;

@RestController
@RequestMapping("/fhir")
public class PatientController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PatientService patientService;

	private List<String> validParamList = Arrays.asList(
			"identifier"
			, "given"
			, "family"
			, "birthdate"
			, "_format"
			, "_pretty");
	
	private String format = "json"; // [FHIR:_format][EDEN:N/A] "json", "xml"
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@GetMapping("/test")
	public String doTest() {		
		String state = "";
		state = "custom";
		
		// use CustomNotFoundException 
		if (state.equalsIgnoreCase("custom")) {
			throw new CustomNotFoundException("force CustomNotFoundException [/fhir/test]");
		}
		
		return "PatientController: Test Hello from /fhir/test endpoint.";
	}
		

	// incoming parameter names are based on HL7/FHIR naming standard
	@GetMapping("/Patient") // "GET" request
	public String getDOHPatients(
				HttpServletRequest request
			   ,HttpServletResponse response
			   ,@RequestParam Optional<String> identifier
			   ,@RequestParam Optional<String> given
			   ,@RequestParam Optional<String> family
			   ,@RequestParam Optional<String> birthdate
			   ,@RequestParam Optional<String> _format) {
				// @RequestParam Optional<String> _pretty) {

		String uri = request.getScheme() + "://" + // "http" + "://
				request.getServerName() + // "localhost"
				":" + // ":"
				request.getServerPort() + // "5151"
				request.getRequestURI() + // "/fhir/Patient"
				"?" + // "?"
				request.getQueryString(); // "given=Ruth&_format=json"
		logger.info("z getRequestURI(): " + uri);

		
		// -------------------------
		// check for invalid parameters
		List<String> l = Collections.list(request.getParameterNames());
		l.removeAll(validParamList);
		if (l.size()>0) {
			OperationOutcome outcome = new OperationOutcome();	
			outcome.getText().setStatusAsString("generated");	// !!! this value has to be "generated"
			outcome.getText().setDivAsString("<div>At least one Unknown search parameter found " + l.toString() + ". Valid value search parameters for this search are: [identifier, given, family, birthdate, _format, _pretty]</div>");
			outcome.addIssue()
					.setSeverity(OperationOutcome.IssueSeverity.ERROR)
					//HttpStatus.BAD_REQUEST
					.setCode(IssueType.NOTSUPPORTED)
					.setDiagnostics("At least one Unknown search parameter found " + l.toString() + ". Valid value search parameters for this search are: [identifier, given, family, birthdate, _format, _pretty]");

			FhirContext ctx = FHIRConfig.getCtx();			
			String outFhirString = ""; 
			if (_format.isPresent() && (_format.get()).equalsIgnoreCase("xml")) {
					outFhirString = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(outcome);	
			}else {
				outFhirString = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(outcome);
			}

			throw new CustomBadRequestException(outFhirString);
		}
		
		
		// -------------------------
		// add all requested parameters into 'build requested parameter' object
		//
		// [todo][refactor]: use java 'build' best practice
		RequestedParameters reqParams = buildRequestedParamsObj(identifier, given, family, birthdate, _format);
		// get data from edenmaster table and return it in a fhir compliance string
		// format [json or xml]
		return patientService.getPatients(reqParams, uri, format);

	}

	/*
	 * 
	 */
	private RequestedParameters buildRequestedParamsObj(Optional<String> identifier, Optional<String> given,
			Optional<String> family, Optional<String> birthdate, Optional<String> _format) {

		RequestedParameters reqParams = new RequestedParameters();

		if (identifier.isPresent()) {
			logger.info("[PatientResource] z@RequestParam identifier.get(): " + identifier.get());
			reqParams.setReq_stateFileNumber(identifier.get().trim());
		} else {
			logger.info("[PatientResource] z@RequestParam identifier.get(): NOT_PRESENT");
		}

		if (given.isPresent()) {
			logger.info("[PatientResource] z@RequestParam given.get():  " + given.get());
			reqParams.setReq_deceasedFirst(given.get().trim());
		} else {
			logger.info("[PatientResource] z@RequestParam given.get(): NOT_PRESENT");
		}

		if (family.isPresent()) {
			logger.info("[PatientResource] z@RequestParam family.get():  " + family.get());
			reqParams.setReq_deceasedLast(family.get().trim());
		} else {
			logger.info("[PatientResource] z@RequestParam family.get(): NOT_PRESENT");
		}

		if (birthdate.isPresent() && isValidDate(birthdate.get())) {
			logger.info("[PatientResource] z@RequestParam birthdate.get():  " + birthdate.get());
			reqParams.setReq_birthdate(birthdate.get().trim());
		} else {
			logger.info("[PatientResource] z@RequestParam birthdate.get(): NOT_PRESENT or INVALID DATE FORMAT");
		}

		if (_format.isPresent()) {
			logger.info("[PatientResource] z@RequestParam format.get():  " + _format.get());
			format = _format.get();
		} else {
			format = "json";
		}
		return reqParams;
	}

	private boolean isValidDate(String input) {
		try {
			simpleDateFormat.parse(input);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

}
