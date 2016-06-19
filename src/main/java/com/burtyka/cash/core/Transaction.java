package com.burtyka.cash.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Transaction implements Serializable {

	private static final long serialVersionUID = -7055283267130801235L;

	double amount;
	float exchangeRate;
	java.lang.String date;
	java.lang.String description;
	java.lang.String fromAccountId;
	java.lang.String id;
	java.lang.String toAccountId;

}
