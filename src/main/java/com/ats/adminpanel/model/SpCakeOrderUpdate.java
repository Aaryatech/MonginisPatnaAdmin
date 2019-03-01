package com.ats.adminpanel.model;

public class SpCakeOrderUpdate{


	private int spOrderNo;
	
	private String spBookedForName;
	
	private int isAllocated;
	
	private String spInstructions;

	public int getSpOrderNo() {
		return spOrderNo;
	}

	public void setSpOrderNo(int spOrderNo) {
		this.spOrderNo = spOrderNo;
	}

	public String getSpBookedForName() {
		return spBookedForName;
	}

	public void setSpBookedForName(String spBookedForName) {
		this.spBookedForName = spBookedForName;
	}

	public int getIsAllocated() {
		return isAllocated;
	}

	public void setIsAllocated(int isAllocated) {
		this.isAllocated = isAllocated;
	}
	
	

	public String getSpInstructions() {
		return spInstructions;
	}

	public void setSpInstructions(String spInstructions) {
		this.spInstructions = spInstructions;
	}

	@Override
	public String toString() {
		return "SpCakeOrderUpdate [spOrderNo=" + spOrderNo + ", spBookedForName=" + spBookedForName + ", isAllocated="
				+ isAllocated + ", spInstructions=" + spInstructions + "]";
	}
	
	
}
