package com.bzvir.report;

import com.burtyka.cash.core.Account;
import com.burtyka.cash.core.Transaction;
import com.burtyka.cash.core.TransactionManager;

import java.util.List;

/**
 * Created by bohdan on 10.04.16.
 */
public class ShortReporter {

    private Account account;
    private TransactionManager transactionManager;

    public ShortReporter(Account account, TransactionManager transactionManager) {
        this.account = account;
        this.transactionManager = transactionManager;
    }

    public String doReport() {
        Account expense = null;

        List<Account> items = account.getItems();
        for (Account item : items) {
            if (item.getName().contains("expenses")) {
                expense = item;
            }
        }
        return printExpenses(expense.getItems());
    }

    private String printExpenses(List<Account> items) {
        StringBuffer report = new StringBuffer();
        for (Account item : items) {
            report.append(item.getName());
            double value = calculate(item);
            report.append("\t\t" + value);
        }
        return report.toString();
    }

    private double calculate(Account item) {
        double sum = 0;
        for (Account account : item.getItems()) {
            System.out.printf("========%s=========%n", account.getName());
            if (account.getItems() !=null && !account.getItems().isEmpty()) {
                for (Account item0 : account.getItems()) {
                    sum += calculate(item0);
                }
            } else {
                printTransactions(account.getId());
            }
        }
        return sum;
    }

    private void printTransactions(String id) {
        List<Transaction> transactions = transactionManager.getTransasctions();
        for (Transaction transaction : transactions) {
            if (transaction.getFromAccountId().equalsIgnoreCase(id)) {
                System.out.println(transaction);
            }
        }
    }
}
