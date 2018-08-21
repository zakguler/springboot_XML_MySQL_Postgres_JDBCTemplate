package com.doh.api.exception_resources;

public class CustomNotFoundException extends RuntimeException {
	
	public CustomNotFoundException(String msg) {
		super(msg);
	}

}
