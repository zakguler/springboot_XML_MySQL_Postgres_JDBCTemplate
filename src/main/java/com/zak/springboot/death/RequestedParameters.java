package com.zak.springboot.death;

public class RequestedParameters {
	
	private String stateFileNumber;
	private String deceasedFirst;
	private String deceasedLast;
	//private String dateOfBirt;
	
	
	public String getStateFileNumber() {
		return stateFileNumber;
	}
	public void setStateFileNumber(String stateFileNumber) {
		this.stateFileNumber = stateFileNumber;
	}
	public String getDeceasedFirst() {
		return deceasedFirst;
	}
	public void setDeceasedFirst(String deceasedFirst) {
		this.deceasedFirst = deceasedFirst;
	}
	public String getDeceasedLast() {
		return deceasedLast;
	}
	public void setDeceasedLast(String deceasedLast) {
		this.deceasedLast = deceasedLast;
	}
	@Override
	public String toString() {
		return "RequestedParameters [stateFileNumber=" + stateFileNumber + ", deceasedFirst=" + deceasedFirst
				+ ", deceasedLast=" + deceasedLast + "]";
	}
	
	
	
}
