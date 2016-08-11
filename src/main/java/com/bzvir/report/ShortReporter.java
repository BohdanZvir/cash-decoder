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
        if (account == null || transactionManager == null) {
            throw new IllegalArgumentException();
        }
        this.account = account;
        this.transactionManager = transactionManager;
    }

    public String doReport() {
        Account expense = getExpenseAccount();

        if (expense == null
                || expense.getItems() == null
                || expense.getItems().isEmpty()) {
            return "";
        }
        StringBuffer report = new StringBuffer();
        return printExpenses(expense.getItems(), report);
    }

    private Account getExpenseAccount() {
        List<Account> items = account.getItems();
        for (Account item : items) {
            if (item.getName().contains("expenses")) {
                return item;
            }
        }
        return null;
    }

    private String printExpenses(List<Account> items, StringBuffer report) {
        for (Account item : items) {
            report
                    .append("\n")
                    .append(item.getName())
                    .append("\n");
            if (isParent(item)) {
                printExpenses(item.getItems(), report);
            } else {
                String id = item.getId();
                printTransactions(id, report);
            }
        }
        return report.toString();
    }

    private boolean isParent(Account account) {
        return account.getItems() !=null && !account.getItems().isEmpty();
    }

    private void printTransactions(String id, StringBuffer report) {
        List<Transaction> transactions = transactionManager.getTransasctions();
        transactions.stream()
                .filter(trans -> trans.getFromAccountId().equalsIgnoreCase(id))
                .forEach(trans -> report.append(trans).append("\n"));
    }
}
