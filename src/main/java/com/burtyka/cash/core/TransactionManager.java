package com.burtyka.cash.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class TransactionManager implements Serializable {

	private static final long serialVersionUID = 3849840293350961550L;

	java.util.Map transactionPositionsToAccounts;
	java.util.ArrayList<Transaction> transasctions;

    public Map getTransactionPositionsToAccounts() {
        return transactionPositionsToAccounts;
    }

    public void setTransactionPositionsToAccounts(Map transactionPositionsToAccounts) {
        this.transactionPositionsToAccounts = transactionPositionsToAccounts;
    }

    public ArrayList<Transaction> getTransasctions() {
        return transasctions;
    }

    public void setTransasctions(ArrayList<Transaction> transasctions) {
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
