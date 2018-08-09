package com.doh.api.fhir;

public class RequestedParameters {
	
	private String req_stateFileNumber;
	private String req_deceasedFirst;
	private String req_deceasedLast;
	private String req_birthdate;	// [yyyy-mm-dd]
	
	
	public String getReq_stateFileNumber() {
		return req_stateFileNumber;
	}
	public void setReq_stateFileNumber(String req_stateFileNumber) {
		this.req_stateFileNumber = req_stateFileNumber;
	}
	public String getReq_deceasedFirst() {
		return req_deceasedFirst;
	}
	public void setReq_deceasedFirst(String req_deceasedFirst) {
		this.req_deceasedFirst = req_deceasedFirst;
	}
	public String getReq_deceasedLast() {
		return req_deceasedLast;
	}
	public void setReq_deceasedLast(String req_deceasedLast) {
		this.req_deceasedLast = req_deceasedLast;
	}
	public String getReq_birthdate() {
		return req_birthdate;
	}
	public void setReq_birthdate(String req_birthdate) {
		this.req_birthdate = req_birthdate;
	}
	
	
	@Override
	public String toString() {
		return "RequestedParameters [req_stateFileNumber=" + req_stateFileNumber + ", req_deceasedFirst="
				+ req_deceasedFirst + ", req_deceasedLast=" + req_deceasedLast + ", req_birthdate=" + req_birthdate
				+ "]";
	}
	
	
}
