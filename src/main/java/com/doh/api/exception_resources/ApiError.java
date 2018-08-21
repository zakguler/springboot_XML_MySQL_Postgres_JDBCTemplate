package com.doh.api.exception_resources;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;

public class ApiError {
	private HttpStatus status;
    private int code;
    private String message;			// user friendly message
    private String systemMessage;	// system message describing the error in more detail
    private Instant timestamp;
    
    private List<ApiSubError> subErrors;

    ApiError(HttpStatus status, Throwable ex) {
        this.status = status;
        this.code = status.value();
        this.message = "Unexpected error";
        this.systemMessage = ex.getLocalizedMessage();
    }

    public ApiError(HttpStatus status, String message) {
        this.status = status;
        this.code = status.value();
        this.message = message;
        this.timestamp = Instant.now();
    }

    public ApiError(HttpStatus status, String message, String systemMessage, Instant timestamp) {
//        this.status = status;
//        this.message = message;
    	this(status, message);
        this.systemMessage = systemMessage;
        this.timestamp = timestamp;
    }

    
	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSystemMessage() {
		return systemMessage;
	}

	public void setSystemMessage(String systemMessage) {
		this.systemMessage = systemMessage;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}


	public List<ApiSubError> getSubErrors() {
		return subErrors;
	}

	public void setSubErrors(List<ApiSubError> subErrors) {
		this.subErrors = subErrors;
	}


	// ApiSubError
	class ApiSubError {
		private String object;
        private String field;
        private Object rejectedValue;
        private String message;

        ApiSubError(String object, String message) {
            this.object = object;
            this.message = message;
        }

		public ApiSubError(String object, String field, Object rejectedValue, String message) {
			super();
			this.object = object;
			this.field = field;
			this.rejectedValue = rejectedValue;
			this.message = message;
		}
        
        
    }
    
}
