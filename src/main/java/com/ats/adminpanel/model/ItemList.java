package com.ats.adminpanel.model;

import java.util.List; 
public class ItemList {
	
	List<GetItem> items;

	public List<GetItem> getItems() {
		return items;
	}

	public void setItems(List<GetItem> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "ItemList [items=" + items + "]";
	}
	
	

}
