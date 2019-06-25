package com.ats.adminpanel.model;

public class ItemReport {

	private int itemId;
	private float billQty;
	private float orderQty;
	private String itemName;
	private int billNo;
	private String invoiceNo;

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public float getBillQty() {
		return billQty;
	}

	public void setBillQty(float billQty) {
		this.billQty = billQty;
	}

	public float getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(float orderQty) {
		this.orderQty = orderQty;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getBillNo() {
		return billNo;
	}

	public void setBillNo(int billNo) {
		this.billNo = billNo;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	@Override
	public String toString() {
		return "ItemReport [itemId=" + itemId + ", billQty=" + billQty + ", orderQty=" + orderQty + ", itemName="
				+ itemName + ", billNo=" + billNo + ", invoiceNo=" + invoiceNo + "]";
	}

}
