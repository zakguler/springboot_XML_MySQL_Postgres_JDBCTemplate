package com.doh.api.exception_resources;

public class CustomBadRequestException extends RuntimeException {
	
	public CustomBadRequestException(String msg) {
		super(msg);
	}

}
