package com.burtyka.cash.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Account implements Serializable {

	private static final long serialVersionUID = 3340655518770206189L;

	int accountDirection;
    int color;
    java.util.List accountList;
    java.lang.String currencyId;
    java.lang.String description;
    java.lang.String id;
    java.util.List<Account> items;
    java.lang.String name;

}
