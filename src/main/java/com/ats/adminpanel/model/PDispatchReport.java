package com.ats.adminpanel.model;

import java.io.Serializable;

public class PDispatchReport implements Serializable{

	
	private int orderId;
		
	private int catId;
	
	private int subCatId;
	
	private String catName;
	
	private int frId;
	
	private String frName;
	
	private int itemId;
	
	private String itemName;
	
	private int orderQty;
	
	private int editQty;

	private int isBillGenerated;

	
	public int getIsBillGenerated() {
		return isBillGenerated;
	}

	public void setIsBillGenerated(int isBillGenerated) {
		this.isBillGenerated = isBillGenerated;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
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

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public int getFrId() {
		return frId;
	}

	public void setFrId(int frId) {
		this.frId = frId;
	}

	public String getFrName() {
		return frName;
	}

	public void setFrName(String frName) {
		this.frName = frName;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(int orderQty) {
		this.orderQty = orderQty;
	}

	public int getEditQty() {
		return editQty;
	}

	public void setEditQty(int editQty) {
		this.editQty = editQty;
	}

	@Override
	public String toString() {
		return "PDispatchReport [orderId=" + orderId + ", catId=" + catId + ", subCatId=" + subCatId + ", catName="
				+ catName + ", frId=" + frId + ", frName=" + frName + ", itemId=" + itemId + ", itemName=" + itemName
				+ ", orderQty=" + orderQty + ", editQty=" + editQty + "]";
	}
	
	
}
