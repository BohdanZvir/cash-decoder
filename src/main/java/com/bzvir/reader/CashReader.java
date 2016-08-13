package com.bzvir.reader;

import com.burtyka.cash.core.*;
import com.bzvir.model.Category;
import com.bzvir.model.Event;
import com.bzvir.report.ShortReporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by bohdan.
 */
public class CashReader implements Reader {

    private static Map<Class, String> files = new HashMap<>();
    static {
        files.put(Settings.class, "settings.dat");
        files.put(Account.class, "account.dat");
        files.put(CurrencyManager.class, "currencyManager.dat");
        files.put(TransactionManager.class, "transactionManager.dat");
    }

    private String dirPath;
    private Account account;
    private TransactionManager transactionManager;

    public CashReader(String dirPath) {
        this.dirPath = dirPath;
        init();
    }

    private void init() {
        account = getValue(Account.class);
        transactionManager = getValue(TransactionManager.class);
    }

    public <T> T getValue(Class<T> clazz) {
        String filename = files.get(clazz);
        String filePath = dirPath + filename;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(filePath)));
            return clazz.cast(ois.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<String> getTitles() {
        return new LinkedHashSet<>(Arrays.asList(
                "Сума у валюті картки",
                "Дата",
                "Час",
                "Опис операції",
                "Категорія",
                "Валюта картки"));
    }

    public List<String> readRowTitles() {
        return null;
    }

    @Override
    public boolean checkTitlesOnPresence() {
        return false;
    }

    @Override
    public List<Event> loadData(Set<String> titles) {
        Account expense = getExpenseAccount();

        if (expense == null
                || expense.getItems() == null
                || expense.getItems().isEmpty()) {
            return new ArrayList<>();
        }
        List<Account> items = expense.getItems();

        LinkedList<Event> events = new LinkedList<>();
        aggregateEvents(items, events);
        return events;
    }

    public Event constructEvent(Account account, Transaction transaction) {
        Event event = new Event();
        event.setProperty("Категорія", account.getName());
        event.setProperty("Дата", transaction.getDate());
        event.setProperty("Час", "");
        event.setProperty("Сума у валюті картки", transaction.getAmount());
        event.setProperty("Опис операції", transaction.getDescription());
        event.setProperty("Валюта картки", account.getCurrencyId());
        event.setProperty("accountId", account.getId());
        return event;
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

    private String aggregateEvents(List<Account> items, LinkedList<Event> events) {
        for (Account item : items) {
            if (isParent(item)) {
                aggregateEvents(item.getItems(), events);
            } else {
                printTransactions(item, events);
            }
        }
        return events.toString();
    }

    private boolean isParent(Account account) {
        return account.getItems() !=null && !account.getItems().isEmpty();
    }

    private void printTransactions(Account item, LinkedList<Event> events) {
        String id = item.getId();
        List<Transaction> transactions = transactionManager.getTransasctions();
        transactions.stream()
                .filter(trans -> trans.getFromAccountId().equalsIgnoreCase(id))
                .forEach(trans -> events.add(constructEvent(item, trans)));
    }

    @Override
    public Map<Category, Double> collectByCategories(LocalDate timeStart, LocalDate timeEnd) {
        return null;
    }

    public static void main(String[] args) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");
        CashReader reader = new CashReader(System.getProperty("user.dir")
                + resourceBundle.getString("sample.dir") + "/");
        Settings settings = reader.getValue(Settings.class);
        Account account = reader.getValue(Account.class);
        TransactionManager transactionManager = reader.getValue(TransactionManager.class);
        CurrencyManager currencyManager = reader.getValue(CurrencyManager.class);

        String report = new ShortReporter(account).doReport();
        System.out.println(report);
    }
}
