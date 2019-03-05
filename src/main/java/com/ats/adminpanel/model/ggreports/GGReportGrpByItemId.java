package com.ats.adminpanel.model.ggreports;

public class GGReportGrpByItemId {
	
	private int itemId;
	private int frId;

	private int isGrn;

	float aprGrandTotal;
	float totalAmt;
	String frName;
	String itemName;
	int reqQty;
	int aprQty;

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getFrId() {
		return frId;
	}

	public void setFrId(int frId) {
		this.frId = frId;
	}

	public int getIsGrn() {
		return isGrn;
	}

	public void setIsGrn(int isGrn) {
		this.isGrn = isGrn;
	}

	public float getAprGrandTotal() {
		return aprGrandTotal;
	}

	public void setAprGrandTotal(float aprGrandTotal) {
		this.aprGrandTotal = aprGrandTotal;
	}

	public float getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(float totalAmt) {
		this.totalAmt = totalAmt;
	}

	public String getFrName() {
		return frName;
	}

	public void setFrName(String frName) {
		this.frName = frName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getReqQty() {
		return reqQty;
	}

	public void setReqQty(int reqQty) {
		this.reqQty = reqQty;
	}

	public int getAprQty() {
		return aprQty;
	}

	public void setAprQty(int aprQty) {
		this.aprQty = aprQty;
	}

	@Override
	public String toString() {
		return "GGReportGrpByItemId [itemId=" + itemId + ", frId=" + frId + ", isGrn=" + isGrn + ", aprGrandTotal="
				+ aprGrandTotal + ", totalAmt=" + totalAmt + ", frName=" + frName + ", itemName=" + itemName
				+ ", reqQty=" + reqQty + ", aprQty=" + aprQty + "]";
	}


}
