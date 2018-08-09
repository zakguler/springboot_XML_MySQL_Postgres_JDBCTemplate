package com.doh.api.fhir.configurations;

import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;

@Component
public class FHIRConfig {

  private static FhirContext ctx;
  static {
	  ctx = FhirContext.forDstu3(); 
  }
  
//  private static IGenericClient client;
//  private static final String serverBase;		//"https://localhost:5151"
  
	public static FhirContext getCtx() {
		return ctx;
	}
}

