package com.bzvir.report;

import com.burtyka.cash.core.Account;

import java.util.List;

/**
 * Created by bohdan.
 */
public class ShortReporter {

    private Account expense;

    public ShortReporter(Account expense) {
        this.expense = expense;
    }

    public String doReport() {
        if (expense == null
                || expense.getItems() == null
                || expense.getItems().isEmpty()) {
            return "";
        }
        StringBuffer report = new StringBuffer();
        return printExpenses(expense.getItems(), report);
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
         report
                 .append("\n")
                 .append(id)
                 .append("\n");
    }
}
