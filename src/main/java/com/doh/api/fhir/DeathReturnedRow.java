package com.doh.api.fhir;

/*
 * this is a list of all columns needed from the edenmaster table
 */
public class DeathReturnedRow {
	
	private String stateFileNumber;
	private String deceasedFirst;
	private String deceasedLast;
	
//	private LocalDateTime dateOfBirth;
	private String birthCCYY;
	private String birthMM;
	private String birthDD;
	
	private String gender;
	
	private String resStreetAdrress1;
	private String resStreetAdrress2;
	private String resCity;
	private String resCounty;
	private String resState;
	private String resZip5;
	private String resCountry;
	
//	private LocalDateTime dateOfDeath;
	private String deathCCYY;
	private String deathMM;
	private String deathDD;
	private String timeOfDeathHH;
	private String timeOfDeathMM;
	
	private String underlyingCode;
	private String contribCode1;
	private String contribCode2;
	private String contribCode3;
	private String contribCode4;
	private String contribCode5;
	private String contribCode6;
	private String contribCode7;
	private String contribCode8;
	private String contribCode9;
	
	private String immediateCause;
	private String additionalCause1;
	private String additionalCause2;
	private String otherConditions;
	private String underlyingCause;
	
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
	
//	public LocalDateTime getDateOfBirth() {
//		return dateOfBirth;
//	}
//	
//	public void setDateOfBirth(LocalDateTime dateOfBirth) {
//		this.dateOfBirth = dateOfBirth;
//	}
	
	public String getBirthCCYY() {
		return birthCCYY;
	}
	
	public void setBirthCCYY(String birthCCYY) {
		this.birthCCYY = birthCCYY;
	}
	
	public String getBirthMM() {
		return birthMM;
	}
	
	public void setBirthMM(String birthMM) {
		this.birthMM = birthMM;
	}
	
	public String getBirthDD() {
		return birthDD;
	}
	
	public void setBirthDD(String birthDD) {
		this.birthDD = birthDD;
	}
	
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getResStreetAdrress1() {
		return resStreetAdrress1;
	}

	public void setResStreetAdrress1(String resStreetAdrress1) {
		this.resStreetAdrress1 = resStreetAdrress1;
	}

	public String getResStreetAdrress2() {
		return resStreetAdrress2;
	}

	public void setResStreetAdrress2(String resStreetAdrress2) {
		this.resStreetAdrress2 = resStreetAdrress2;
	}

	public String getResCity() {
		return resCity;
	}

	public void setResCity(String resCity) {
		this.resCity = resCity;
	}

	public String getResCounty() {
		return resCounty;
	}

	public void setResCounty(String resCounty) {
		this.resCounty = resCounty;
	}

	public String getResState() {
		return resState;
	}

	public void setResState(String resState) {
		this.resState = resState;
	}

	public String getResZip5() {
		return resZip5;
	}

	public void setResZip5(String resZip5) {
		this.resZip5 = resZip5;
	}

	public String getResCountry() {
		return resCountry;
	}

	public void setResCountry(String resCountry) {
		this.resCountry = resCountry;
	}

	
//	public LocalDateTime getDateOfDeath() {
//		return dateOfDeath;
//	}
//
//	public void setDateOfDeath(LocalDateTime dateOfDeath) {
//		this.dateOfDeath = dateOfDeath;
//	}

	public String getDeathCCYY() {
		return deathCCYY;
	}

	public void setDeathCCYY(String deathCCYY) {
		this.deathCCYY = deathCCYY;
	}

	public String getDeathMM() {
		return deathMM;
	}

	public void setDeathMM(String deathMM) {
		this.deathMM = deathMM;
	}

	public String getDeathDD() {
		return deathDD;
	}

	public void setDeathDD(String deathDD) {
		this.deathDD = deathDD;
	}

	
	
	public String getTimeOfDeathHH() {
		return timeOfDeathHH;
	}

	public void setTimeOfDeathHH(String timeOfDeathHH) {
		this.timeOfDeathHH = timeOfDeathHH;
	}

	public String getTimeOfDeathMM() {
		return timeOfDeathMM;
	}

	public void setTimeOfDeathMM(String timeOfDeathMM) {
		this.timeOfDeathMM = timeOfDeathMM;
	}

	public String getUnderlyingCode() {
		return underlyingCode;
	}

	public void setUnderlyingCode(String underlyingCode) {
		this.underlyingCode = underlyingCode;
	}

	public String getContribCode1() {
		return contribCode1;
	}

	public void setContribCode1(String contribCode1) {
		this.contribCode1 = contribCode1;
	}

	public String getContribCode2() {
		return contribCode2;
	}

	public void setContribCode2(String contribCode2) {
		this.contribCode2 = contribCode2;
	}

	public String getContribCode3() {
		return contribCode3;
	}

	public void setContribCode3(String contribCode3) {
		this.contribCode3 = contribCode3;
	}

	public String getContribCode4() {
		return contribCode4;
	}

	public void setContribCode4(String contribCode4) {
		this.contribCode4 = contribCode4;
	}

	public String getContribCode5() {
		return contribCode5;
	}

	public void setContribCode5(String contribCode5) {
		this.contribCode5 = contribCode5;
	}

	public String getContribCode6() {
		return contribCode6;
	}

	public void setContribCode6(String contribCode6) {
		this.contribCode6 = contribCode6;
	}

	public String getContribCode7() {
		return contribCode7;
	}

	public void setContribCode7(String contribCode7) {
		this.contribCode7 = contribCode7;
	}

	public String getContribCode8() {
		return contribCode8;
	}

	public void setContribCode8(String contribCode8) {
		this.contribCode8 = contribCode8;
	}

	public String getContribCode9() {
		return contribCode9;
	}

	public void setContribCode9(String contribCode9) {
		this.contribCode9 = contribCode9;
	}

	public String getImmediateCause() {
		return immediateCause;
	}

	public void setImmediateCause(String immediateCause) {
		this.immediateCause = immediateCause;
	}

	public String getAdditionalCause1() {
		return additionalCause1;
	}

	public void setAdditionalCause1(String additionalCause1) {
		this.additionalCause1 = additionalCause1;
	}

	public String getAdditionalCause2() {
		return additionalCause2;
	}

	public void setAdditionalCause2(String additionalCause2) {
		this.additionalCause2 = additionalCause2;
	}

	public String getOtherConditions() {
		return otherConditions;
	}

	public void setOtherConditions(String otherConditions) {
		this.otherConditions = otherConditions;
	}

	public String getUnderlyingCause() {
		return underlyingCause;
	}

	public void setUnderlyingCause(String underlyingCause) {
		this.underlyingCause = underlyingCause;
	}

	@Override
	public String toString() {
		return "DeathReturnedRow [stateFileNumber=" + stateFileNumber + ", deceasedFirst=" + deceasedFirst
				+ ", deceasedLast=" + deceasedLast + ", birthCCYY=" + birthCCYY + ", birthMM=" + birthMM + ", birthDD="
				+ birthDD + ", gender=" + gender + ", resStreetAdrress1=" + resStreetAdrress1 + ", resStreetAdrress2="
				+ resStreetAdrress2 + ", resCity=" + resCity + ", resCounty=" + resCounty + ", resState=" + resState
				+ ", resZip5=" + resZip5 + ", resCountry=" + resCountry + ", deathCCYY=" + deathCCYY + ", deathMM="
				+ deathMM + ", deathDD=" + deathDD + ", timeOfDeathHH=" + timeOfDeathHH + ", timeOfDeathMM="
				+ timeOfDeathMM + ", underlyingCode=" + underlyingCode + ", contribCode1=" + contribCode1
				+ ", contribCode2=" + contribCode2 + ", contribCode3=" + contribCode3 + ", contribCode4=" + contribCode4
				+ ", contribCode5=" + contribCode5 + ", contribCode6=" + contribCode6 + ", contribCode7=" + contribCode7
				+ ", contribCode8=" + contribCode8 + ", contribCode9=" + contribCode9 + ", immediateCause="
				+ immediateCause + ", additionalCause1=" + additionalCause1 + ", additionalCause2=" + additionalCause2
				+ ", otherConditions=" + otherConditions + ", underlyingCause=" + underlyingCause + "]";
	}


}
