package com.burtyka.cash.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CurrencyManager  implements Serializable {

	private static final long serialVersionUID = 4860247926977364725L;

	java.util.ArrayList<Currency> currencies;
    java.lang.String defaultCurrencyId;

}
