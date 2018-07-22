package com.ats.adminpanel.model;

import java.util.List;

import com.ats.adminpanel.model.franchisee.FrNameIdByRouteId;
import com.ats.adminpanel.model.franchisee.SubCategory;
import com.ats.adminpanel.model.item.Item;

public class PDispatchReportList {
	
	private List<PDispatchReport> dispatchReportList;
	private List<Item> itemList;
	private List<FrNameIdByRouteId>  frList;
	private List<SubCategory>  subCatList;
	public List<PDispatchReport> getDispatchReportList() {
		return dispatchReportList;
	}
	public void setDispatchReportList(List<PDispatchReport> dispatchReportList) {
		this.dispatchReportList = dispatchReportList;
	}
	public List<Item> getItemList() {
		return itemList;
	}
	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}
	public List<FrNameIdByRouteId> getFrList() {
		return frList;
	}
	public void setFrList(List<FrNameIdByRouteId> frList) {
		this.frList = frList;
	}
	public List<SubCategory> getSubCatList() {
		return subCatList;
	}
	public void setSubCatList(List<SubCategory> subCatList) {
		this.subCatList = subCatList;
	}
	@Override
	public String toString() {
		return "PDispatchReportList [dispatchReportList=" + dispatchReportList + ", itemList=" + itemList + ", frList="
				+ frList + ", subCatList=" + subCatList + "]";
	}
	
	

}
