package com.ats.adminpanel.model;


public class FranchiseForDispatch {
	
    private int frId;
	
	private int frRouteId;
	
	private String frName;
	
	private int frRate;
	
	private int frRateCat;

	public int getFrId() {
		return frId;
	}

	public void setFrId(int frId) {
		this.frId = frId;
	}

	public int getFrRouteId() {
		return frRouteId;
	}

	public void setFrRouteId(int frRouteId) {
		this.frRouteId = frRouteId;
	}

	public String getFrName() {
		return frName;
	}

	public void setFrName(String frName) {
		this.frName = frName;
	}

	public int getFrRate() {
		return frRate;
	}

	public void setFrRate(int frRate) {
		this.frRate = frRate;
	}

	public int getFrRateCat() {
		return frRateCat;
	}

	public void setFrRateCat(int frRateCat) {
		this.frRateCat = frRateCat;
	}

	@Override
	public String toString() {
		return "FranchiseForDispatch [frId=" + frId + ", frRouteId=" + frRouteId + ", frName=" + frName + ", frRate="
				+ frRate + ", frRateCat=" + frRateCat + "]";
	}

	
}
