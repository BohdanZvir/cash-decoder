package com.burtyka.cash.core;

import java.io.Serializable;

public class Currency  implements Serializable {

	private static final long serialVersionUID = 4037227943016334848L;

    java.lang.String abbreviation;
    java.lang.String id;
    java.lang.String name;
    
	public java.lang.String getAbbreviation() {
		return abbreviation;
	}
	public void setAbbreviation(java.lang.String abbreviation) {
		this.abbreviation = abbreviation;
	}
	public java.lang.String getId() {
		return id;
	}
	public void setId(java.lang.String id) {
		this.id = id;
	}
	public java.lang.String getName() {
		return name;
	}
	public void setName(java.lang.String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Currency [abbreviation=" + abbreviation + ", id=" + id + ", name=" + name + "]";
	}
	
}
