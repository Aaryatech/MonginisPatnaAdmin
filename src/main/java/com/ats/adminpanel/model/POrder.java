package com.ats.adminpanel.model;


public class POrder {

     private int orderId;
	
	 private int editQty;

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getEditQty() {
		return editQty;
	}

	public void setEditQty(int editQty) {
		this.editQty = editQty;
	}

	@Override
	public String toString() {
		return "POrder [orderId=" + orderId + ", editQty=" + editQty + "]";
	}
	 
	 
}
