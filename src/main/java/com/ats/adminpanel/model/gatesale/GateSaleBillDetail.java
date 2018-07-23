package com.ats.adminpanel.model.gatesale;


public class GateSaleBillDetail {

	
	private int billDetailId;
	
	private int billId;
	
	private int itemId;

	
	private float itemQty;
	
	private float itemRate;
	
	private float itemValue;

	public int getBillDetailId() {
		return billDetailId;
	}

	public void setBillDetailId(int billDetailId) {
		this.billDetailId = billDetailId;
	}

	public int getBillId() {
		return billId;
	}

	public void setBillId(int billId) {
		this.billId = billId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public float getItemQty() {
		return itemQty;
	}

	public void setItemQty(float itemQty) {
		this.itemQty = itemQty;
	}

	public float getItemRate() {
		return itemRate;
	}

	public void setItemRate(float itemRate) {
		this.itemRate = itemRate;
	}

	public float getItemValue() {
		return itemValue;
	}

	public void setItemValue(float itemValue) {
		this.itemValue = itemValue;
	}

	@Override
	public String toString() {
		return "GateSaleBillDetail [billDetailId=" + billDetailId + ", billId=" + billId + ", itemId=" + itemId
				+ ", itemQty=" + itemQty + ", itemRate=" + itemRate + ", itemValue=" + itemValue + "]";
	}

    
}
