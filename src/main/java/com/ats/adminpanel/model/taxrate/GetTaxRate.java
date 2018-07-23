package com.ats.adminpanel.model.taxrate;

public class GetTaxRate  {

	private int taxRateId;
	
	private String hsnCode;
	
	private float cgstPer;
	
	private float sgstPer;
	
	private float igstPer;
	
	private String uom;
	
	private String subCatName;

	public int getTaxRateId() {
		return taxRateId;
	}

	public String getHsnCode() {
		return hsnCode;
	}

	public float getCgstPer() {
		return cgstPer;
	}

	public float getSgstPer() {
		return sgstPer;
	}

	public float getIgstPer() {
		return igstPer;
	}

	public String getUom() {
		return uom;
	}

	public String getSubCatName() {
		return subCatName;
	}

	public void setTaxRateId(int taxRateId) {
		this.taxRateId = taxRateId;
	}

	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}

	public void setCgstPer(float cgstPer) {
		this.cgstPer = cgstPer;
	}

	public void setSgstPer(float sgstPer) {
		this.sgstPer = sgstPer;
	}

	public void setIgstPer(float igstPer) {
		this.igstPer = igstPer;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public void setSubCatName(String subCatName) {
		this.subCatName = subCatName;
	}

	@Override
	public String toString() {
		return "GetTaxRate [taxRateId=" + taxRateId + ", hsnCode=" + hsnCode + ", cgstPer=" + cgstPer + ", sgstPer="
				+ sgstPer + ", igstPer=" + igstPer + ", uom=" + uom + ", subCatName=" + subCatName + "]";
	}
	
	
}
