package com.ats.adminpanel.model;

public class GetSubCat {
	
	private int subCatId;
	private String subCatName;
	private int catId;
	public int getSubCatId() {
		return subCatId;
	}
	public void setSubCatId(int subCatId) {
		this.subCatId = subCatId;
	}
	public String getSubCatName() {
		return subCatName;
	}
	public void setSubCatName(String subCatName) {
		this.subCatName = subCatName;
	}
	public int getCatId() {
		return catId;
	}
	public void setCatId(int catId) {
		this.catId = catId;
	}
	@Override
	public String toString() {
		return "GetSubCat [subCatId=" + subCatId + ", subCatName=" + subCatName + ", catId=" + catId + "]";
	}
	
	
}
