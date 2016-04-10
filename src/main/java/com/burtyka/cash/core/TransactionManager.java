package com.burtyka.cash.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TransactionManager implements Serializable {

	private static final long serialVersionUID = 3849840293350961550L;

	java.util.Map transactionPositionsToAccounts;
	java.util.List<Transaction> transasctions;

    public Map getTransactionPositionsToAccounts() {
        return transactionPositionsToAccounts;
    }

    public void setTransactionPositionsToAccounts(Map transactionPositionsToAccounts) {
        this.transactionPositionsToAccounts = transactionPositionsToAccounts;
    }

    public List<Transaction> getTransasctions() {
        return transasctions;
    }

    public void setTransasctions(List<Transaction> transasctions) {
        this.transasctions = transasctions;
    }

    @Override
    public String toString() {
        return "TransactionManager{" +
                "transactionPositionsToAccounts=" + transactionPositionsToAccounts +
                ", transasctions=" + transasctions +
                '}';
    }
}
