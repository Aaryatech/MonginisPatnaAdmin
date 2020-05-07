package com.ats.adminpanel.model.salesreport;


public class SalesReportRoyalty {
	
	private String uid;
	private int id;
	
	private int catId;
	private int subCatId;
	private String item_name;
	private String cat_name;
	float tBillQty;
	float tBillTaxableAmt;
	float tGrnQty;
	float tGrnTaxableAmt;
	float tGvnQty;
	float tGvnTaxableAmt;
	
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getSubCatId() {
		return subCatId;
	}
	public void setSubCatId(int subCatId) {
		this.subCatId = subCatId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCatId() {
		return catId;
	}
	public void setCatId(int catId) {
		this.catId = catId;
	}
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public String getCat_name() {
		return cat_name;
	}
	public void setCat_name(String cat_name) {
		this.cat_name = cat_name;
	}
	public float gettBillQty() {
		return tBillQty;
	}
	public void settBillQty(float tBillQty) {
		this.tBillQty = tBillQty;
	}
	public float gettBillTaxableAmt() {
		return tBillTaxableAmt;
	}
	public void settBillTaxableAmt(float tBillTaxableAmt) {
		this.tBillTaxableAmt = tBillTaxableAmt;
	}
	public float gettGrnQty() {
		return tGrnQty;
	}
	public void settGrnQty(float tGrnQty) {
		this.tGrnQty = tGrnQty;
	}
	public float gettGrnTaxableAmt() {
		return tGrnTaxableAmt;
	}
	public void settGrnTaxableAmt(float tGrnTaxableAmt) {
		this.tGrnTaxableAmt = tGrnTaxableAmt;
	}
	public float gettGvnQty() {
		return tGvnQty;
	}
	public void settGvnQty(float tGvnQty) {
		this.tGvnQty = tGvnQty;
	}
	public float gettGvnTaxableAmt() {
		return tGvnTaxableAmt;
	}
	public void settGvnTaxableAmt(float tGvnTaxableAmt) {
		this.tGvnTaxableAmt = tGvnTaxableAmt;
	}
	@Override
	public String toString() {
		return "SalesReportRoyalty [uid=" + uid + ", id=" + id + ", catId=" + catId + ", subCatId=" + subCatId
				+ ", item_name=" + item_name + ", cat_name=" + cat_name + ", tBillQty=" + tBillQty
				+ ", tBillTaxableAmt=" + tBillTaxableAmt + ", tGrnQty=" + tGrnQty + ", tGrnTaxableAmt=" + tGrnTaxableAmt
				+ ", tGvnQty=" + tGvnQty + ", tGvnTaxableAmt=" + tGvnTaxableAmt + "]";
	}
	
}
