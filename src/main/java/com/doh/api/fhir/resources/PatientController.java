package com.doh.api.fhir.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doh.api.fhir.RequestedParameters;
import com.doh.api.fhir.services.PatientService;

@RestController
@RequestMapping("/fhir")
public class PatientController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PatientService patientService;

	private String format = "json"; // [FHIR:_format][EDEN:N/A] "json", "xml"
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@GetMapping("/test") // <=============================================================================================
							// "GET" request
	public String getDOHPatients() {
		return "PatientController: Test Hello from /fhir/test endpoint.";
	}

	// incoming parameter names are based on HL7/FHIR naming standard
	@GetMapping("/Patient") // <=====================================================================================
							// "GET" request
	public String getDOHPatients(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Optional<String> identifier, @RequestParam Optional<String> given,
			@RequestParam Optional<String> family, @RequestParam Optional<String> birthdate,
			@RequestParam Optional<String> _format) {
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
		// validate resource type
		//

		if (!request.getRequestURI().contains("Patient")) {
			logger.info("Unknown resource type ??? '" + request.getRequestURI() + "' - Server knows how to handle the following: [Patient]");
//			return "Unknown resource type '" + request.getRequestURI() + "' - Server knows how to handle: [Patient]";
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
