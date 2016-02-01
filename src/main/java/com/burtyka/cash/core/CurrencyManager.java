package com.burtyka.cash.core;

import java.io.Serializable;

public class CurrencyManager  implements Serializable {

	private static final long serialVersionUID = 4860247926977364725L
			;

	java.util.ArrayList<Currency> currencies;
    java.lang.String defaultCurrencyId;

    public java.util.ArrayList<Currency> getCurrencies() {
		return currencies;
	}
	public void setCurrencies(java.util.ArrayList<Currency> currencies) {
		this.currencies = currencies;
	}
	public java.lang.String getDefaultCurrencyId() {
		return defaultCurrencyId;
	}
	public void setDefaultCurrencyId(java.lang.String defaultCurrencyId) {
		this.defaultCurrencyId = defaultCurrencyId;
	}
	
	@Override
	public String toString() {
		return "CurrencyManager [currencies=" + currencies + ", defaultCurrencyId=" + defaultCurrencyId + "]";
	}
	
}
