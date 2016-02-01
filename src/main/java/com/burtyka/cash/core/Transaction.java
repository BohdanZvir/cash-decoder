package com.burtyka.cash.core;

import java.io.Serializable;

public class Transaction implements Serializable {

	private static final long serialVersionUID = -7055283267130801235L;

	double amount;
	float exchangeRate;
	java.lang.String date;
	java.lang.String description;
	java.lang.String fromAccountId;
	java.lang.String id;
	java.lang.String toAccountId;

    public String getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(float exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", exchangeRate=" + exchangeRate +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", fromAccountId='" + fromAccountId + '\'' +
                ", id='" + id + '\'' +
                ", toAccountId='" + toAccountId + '\'' +
                '}';
    }
}
