package com.ats.adminpanel.model;

import java.util.Date;

public class TaxHsn {
	
	
	 private int taxHsnId;
	
	private int catId;
	private int subCatId;
	
	private float cgstPer;
	private float sgstPer;
	private float igstPer;
	
	private String hsnCode;
	
	private Date addDate;
	private Date editDate;
	
	private int delStatus;

	public int getTaxHsnId() {
		return taxHsnId;
	}

	public void setTaxHsnId(int taxHsnId) {
		this.taxHsnId = taxHsnId;
	}

	public int getCatId() {
		return catId;
	}

	public void setCatId(int catId) {
		this.catId = catId;
	}

	public int getSubCatId() {
		return subCatId;
	}

	public void setSubCatId(int subCatId) {
		this.subCatId = subCatId;
	}

	public float getCgstPer() {
		return cgstPer;
	}

	public void setCgstPer(float cgstPer) {
		this.cgstPer = cgstPer;
	}

	public float getSgstPer() {
		return sgstPer;
	}

	public void setSgstPer(float sgstPer) {
		this.sgstPer = sgstPer;
	}

	public float getIgstPer() {
		return igstPer;
	}

	public void setIgstPer(float igstPer) {
		this.igstPer = igstPer;
	}

	public String getHsnCode() {
		return hsnCode;
	}

	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Date getEditDate() {
		return editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public int getDelStatus() {
		return delStatus;
	}

	public void setDelStatus(int delStatus) {
		this.delStatus = delStatus;
	}

	@Override
	public String toString() {
		return "TaxHsn [taxHsnId=" + taxHsnId + ", catId=" + catId + ", subCatId=" + subCatId + ", cgstPer=" + cgstPer
				+ ", sgstPer=" + sgstPer + ", igstPer=" + igstPer + ", hsnCode=" + hsnCode + ", addDate=" + addDate
				+ ", editDate=" + editDate + ", delStatus=" + delStatus + "]";
	}
	

}
