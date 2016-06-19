package com.burtyka.cash.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class TransactionManager implements Serializable {

	private static final long serialVersionUID = 3849840293350961550L;

	java.util.Map transactionPositionsToAccounts;
	java.util.List<Transaction> transasctions;

}
