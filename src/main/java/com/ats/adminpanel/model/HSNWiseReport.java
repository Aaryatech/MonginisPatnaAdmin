package com.ats.adminpanel.model;

import java.util.Date;

public class HSNWiseReport {

	private String itemHsncd;

	private float itemTax1;

	private float itemTax2;

	private float billQty;
	private float taxableAmt;

	private float cgstRs;
	private float sgstRs;
	private float grnGvnQty;

	public String getItemHsncd() {
		return itemHsncd;
	}

	public void setItemHsncd(String itemHsncd) {
		this.itemHsncd = itemHsncd;
	}

	public float getItemTax1() {
		return itemTax1;
	}

	public void setItemTax1(float itemTax1) {
		this.itemTax1 = itemTax1;
	}

	public float getItemTax2() {
		return itemTax2;
	}

	public void setItemTax2(float itemTax2) {
		this.itemTax2 = itemTax2;
	}

	public float getBillQty() {
		return billQty;
	}

	public void setBillQty(float billQty) {
		this.billQty = billQty;
	}

	public float getTaxableAmt() {
		return taxableAmt;
	}

	public void setTaxableAmt(float taxableAmt) {
		this.taxableAmt = taxableAmt;
	}

	public float getCgstRs() {
		return cgstRs;
	}

	public void setCgstRs(float cgstRs) {
		this.cgstRs = cgstRs;
	}

	public float getSgstRs() {
		return sgstRs;
	}

	public void setSgstRs(float sgstRs) {
		this.sgstRs = sgstRs;
	}

	public float getGrnGvnQty() {
		return grnGvnQty;
	}

	public void setGrnGvnQty(float grnGvnQty) {
		this.grnGvnQty = grnGvnQty;
	}

	@Override
	public String toString() {
		return "HSNWiseReport [itemHsncd=" + itemHsncd + ", itemTax1=" + itemTax1 + ", itemTax2=" + itemTax2
				+ ", billQty=" + billQty + ", taxableAmt=" + taxableAmt + ", cgstRs=" + cgstRs + ", sgstRs=" + sgstRs
				+ ", grnGvnQty=" + grnGvnQty + "]";
	}

}
