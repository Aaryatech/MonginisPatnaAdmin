package com.ats.adminpanel.model.gatesale;

import java.util.Date;
import java.util.List;

public class GateSaleBillHeader {

	
	private int billId;
	
	
	private Date billDate;
	
	
	private int category;
	
	
	private int isOther;
	
	
	private String invoiceNo;
	
	
	private String custName;
	
	
	private int empId;
	
	
	private float discountPer;
	
	private float billAmt;
	
	private float discountAmt;
	
	private float roundOff;
	
	private float billGrantAmt;
	
	private int isApproved;
	
	private Date approvedDate;
	
	private int approvedUserId;
	
	private int amtIsCollected;
	
	private Date collectedDate;
	
	private int collectedUserId;
	
	private int isBillPrint;
	
	private int initiatorUserId;
	
	private int delStatus;

	List<GateSaleBillDetail> gateSaleBillDetailList;
	
	public List<GateSaleBillDetail> getGateSaleBillDetailList() {
		return gateSaleBillDetailList;
	}

	public void setGateSaleBillDetailList(List<GateSaleBillDetail> gateSaleBillDetailList) {
		this.gateSaleBillDetailList = gateSaleBillDetailList;
	}
    
	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public int getBillId() {
		return billId;
	}
	public void setBillId(int billId) {
		this.billId = billId;
	}
	public Date getBillDate() {
		return billDate;
	}
	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getIsOther() {
		return isOther;
	}

	public void setIsOther(int isOther) {
		this.isOther = isOther;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public int getEmpId() {
		return empId;
	}

	public void setEmpId(int empId) {
		this.empId = empId;
	}

	public float getDiscountPer() {
		return discountPer;
	}

	public void setDiscountPer(float discountPer) {
		this.discountPer = discountPer;
	}

	public float getBillAmt() {
		return billAmt;
	}

	public void setBillAmt(float billAmt) {
		this.billAmt = billAmt;
	}

	public float getDiscountAmt() {
		return discountAmt;
	}

	public void setDiscountAmt(float discountAmt) {
		this.discountAmt = discountAmt;
	}

	public float getRoundOff() {
		return roundOff;
	}

	public void setRoundOff(float roundOff) {
		this.roundOff = roundOff;
	}

	public float getBillGrantAmt() {
		return billGrantAmt;
	}

	public void setBillGrantAmt(float billGrantAmt) {
		this.billGrantAmt = billGrantAmt;
	}

	public int getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(int isApproved) {
		this.isApproved = isApproved;
	}
	public Date getApprovedDate() {
		return approvedDate;
	}
	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public int getApprovedUserId() {
		return approvedUserId;
	}

	public void setApprovedUserId(int approvedUserId) {
		this.approvedUserId = approvedUserId;
	}

	public int getAmtIsCollected() {
		return amtIsCollected;
	}

	public void setAmtIsCollected(int amtIsCollected) {
		this.amtIsCollected = amtIsCollected;
	}
	public Date getCollectedDate() {
		return collectedDate;
	}
	public void setCollectedDate(Date collectedDate) {
		this.collectedDate = collectedDate;
	}

	public int getCollectedUserId() {
		return collectedUserId;
	}

	public void setCollectedUserId(int collectedUserId) {
		this.collectedUserId = collectedUserId;
	}

	public int getIsBillPrint() {
		return isBillPrint;
	}

	public void setIsBillPrint(int isBillPrint) {
		this.isBillPrint = isBillPrint;
	}

	public int getInitiatorUserId() {
		return initiatorUserId;
	}

	public void setInitiatorUserId(int initiatorUserId) {
		this.initiatorUserId = initiatorUserId;
	}

	public int getDelStatus() {
		return delStatus;
	}

	public void setDelStatus(int delStatus) {
		this.delStatus = delStatus;
	}

	@Override
	public String toString() {
		return "GateSaleBillHeader [billId=" + billId + ", billDate=" + billDate + ", category=" + category
				+ ", isOther=" + isOther + ", invoiceNo=" + invoiceNo + ", custName=" + custName + ", empId=" + empId
				+ ", discountPer=" + discountPer + ", billAmt=" + billAmt + ", discountAmt=" + discountAmt
				+ ", roundOff=" + roundOff + ", billGrantAmt=" + billGrantAmt + ", isApproved=" + isApproved
				+ ", approvedDate=" + approvedDate + ", approvedUserId=" + approvedUserId + ", amtIsCollected="
				+ amtIsCollected + ", collectedDate=" + collectedDate + ", collectedUserId=" + collectedUserId
				+ ", isBillPrint=" + isBillPrint + ", initiatorUserId=" + initiatorUserId + ", delStatus=" + delStatus
				+ ", gateSaleBillDetailList=" + gateSaleBillDetailList + "]";
	}

	

}
