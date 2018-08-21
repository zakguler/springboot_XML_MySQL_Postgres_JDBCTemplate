package com.doh.api.exception_resources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.doh.api.fhir.configurations.FHIRConfig;

import ca.uhn.fhir.context.FhirContext;

//#==============================================================
//# Override default Exception handler
//# used with custom Exception handler 
//spring.mvc.throw-exception-if-no-handler-found=true
//spring.resources.add-mappings=false

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@ExceptionHandler(CustomBadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleNotFoundException_3(CustomBadRequestException ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		String message = ex.getMessage();              
		return message;
	}

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String zCustomNoHandlerFoundException(
    						NoHandlerFoundException ex
    						,HttpServletRequest request
    						,HttpServletResponse response) {
    	
    	// http://localhost:5151/fhir/xPatient?given=Ruth&_format=json
    	String uri = request.getScheme() + "://" + // "http" + "://
				request.getServerName() + // "localhost"
				":" + // ":"
				request.getServerPort() + // "5151"
				request.getRequestURI() + // "/fhir/Patient"
				"?" + // "?"
				request.getQueryString(); // "given=Ruth&_format=json"
		logger.info("z getRequestURI(): " + uri);

		// extract the resource type from the URI path EX: "Patient" from "/fhir/Patient"
		String URIresourceType = (request.getRequestURI()).substring(request.getRequestURI().lastIndexOf("/")+1);
		logger.info("z URIresourceType: " + URIresourceType);
				
		OperationOutcome outcome = new OperationOutcome();	
		outcome.getText().setStatusAsString("generated");	// !!! this value has to be "generated"
		outcome.getText().setDivAsString("<div>Unknown resource type [" + URIresourceType + "] - Server knows how to handle the following(s): [Patient]</div>");
		
		outcome.addIssue()
				.setSeverity(OperationOutcome.IssueSeverity.ERROR)
				.setCode(IssueType.NOTFOUND)
				.setDiagnostics("<div>Unknown resource type [" + URIresourceType + "] - Server knows how to handle the following(s): [Patient]</div>");

		FhirContext ctx = FHIRConfig.getCtx();			
		String outFhirString = ""; 

		String format = request.getParameter("_format");
		logger.info("z : " + format);
		
		if (format != null && format.equalsIgnoreCase("xml")) {
			outFhirString = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(outcome);	
		}else {
			outFhirString = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(outcome);
		}
		
		return outFhirString;
    }


    
    // EX: throw new CustomNotFoundException("Zak.. force CustomNotFoundException [/fhir/test]");
	@ExceptionHandler(CustomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleNotFoundException_2(CustomNotFoundException ex) {
//    	logger.info("zak.. HttpStatus.NOT_FOUND: " + HttpStatus.NOT_FOUND);
    	HttpStatus status = HttpStatus.NOT_FOUND;
        String message = ex.getMessage();
        return new ApiError(status, message);
	}
	
    
}
