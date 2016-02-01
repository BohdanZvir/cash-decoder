package com.burtyka.cash.core;

import java.io.Serializable;

public class Account implements Serializable {

	private static final long serialVersionUID = 3340655518770206189L;

	int accountDirection;
    int color;
    java.util.ArrayList accountList;
    java.lang.String currencyId;
    java.lang.String description;
    java.lang.String id;
    java.util.ArrayList items;
    java.lang.String name;
    
	public int getAccountDirection() {
		return accountDirection;
	}
	public void setAccountDirection(int accountDirection) {
		this.accountDirection = accountDirection;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public java.util.ArrayList getAccountList() {
		return accountList;
	}
	public void setAccountList(java.util.ArrayList accountList) {
		this.accountList = accountList;
	}
	public java.lang.String getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(java.lang.String currencyId) {
		this.currencyId = currencyId;
	}
	public java.lang.String getDescription() {
		return description;
	}
	public void setDescription(java.lang.String description) {
		this.description = description;
	}
	public java.lang.String getId() {
		return id;
	}
	public void setId(java.lang.String id) {
		this.id = id;
	}
	public java.util.ArrayList getItems() {
		return items;
	}
	public void setItems(java.util.ArrayList items) {
		this.items = items;
	}
	public java.lang.String getName() {
		return name;
	}
	public void setName(java.lang.String name) {
		this.name = name;
	}
	
    @Override
	public String toString() {
		return "{accountDirection=" + accountDirection + ", color=" + color + ", accountList=" + accountList
				+ ", currencyId=\"" + currencyId + "\", description=\"" + description + "\", id=\"" + id + "\", items=" + items
				+ ", name=\"" + name + "\"}";
	}
}
