
package com.ats.adminpanel.model.item;


public class SubCategory {

    private int subCatId;
    private String subCatName;
    private int delStatus;
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
	public int getDelStatus() {
		return delStatus;
	}
	public void setDelStatus(int delStatus) {
		this.delStatus = delStatus;
	}
	public int getCatId() {
		return catId;
	}
	public void setCatId(int catId) {
		this.catId = catId;
	}
	@Override
	public String toString() {
		return "SubCategory [subCatId=" + subCatId + ", subCatName=" + subCatName + ", delStatus=" + delStatus
				+ ", catId=" + catId + "]";
	}
    
    

}
