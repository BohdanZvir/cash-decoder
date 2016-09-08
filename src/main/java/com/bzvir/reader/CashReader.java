package com.bzvir.reader;

import com.burtyka.cash.core.*;
import com.bzvir.model.Event;
import com.bzvir.util.EventJoiner;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public boolean checkTitlesOnPresence() {
        return true;
    }

    @Override
    public List<Event> loadData() {
        Account expense = getExpenseAccount();

        if (expense == null
                || expense.getItems() == null
                || expense.getItems().isEmpty()) {
            throw new RuntimeException("There are no items to work with.");
        }
        List<Account> items = expense.getItems();
        return aggregateEvents(items);
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

    public List<Event> aggregateEvents(List<Account> items) {
        List<Event> events = new ArrayList<>();
        List<Event> list;
        for (Account item : items) {
            if (isParent(item)) {
                list = aggregateEvents(item.getItems());
            } else {
                list = findTransactions(item);
            }
            events.addAll(list);
        }
        return events;
    }

    public static boolean isParent(Account account) {
        return account.getItems() != null && !account.getItems().isEmpty();
    }

    public List<Event> findTransactions(Account item) {
        String id = item.getId();
        return getTransactions().stream()
                .filter(trans -> trans.getFromAccountId().equalsIgnoreCase(id))
                .map(trans ->  constructEvent(item, trans))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactions() {
        return transactionManager.getTransasctions();
    }

    @Override
    public Account reverseConvert(List<Event> p24) {
        List<Transaction> transactions = null;
        Map<String, List<Event>> grouped = EventJoiner.groupByCategory(p24);
        for (Map.Entry<String, List<Event>> entry : grouped.entrySet()) {
            Account created = createAccount(entry.getKey());

            List<Event> events = entry.getValue();
            transactions = createTransactions(events, created.getId());
        }
        if (transactions != null) {
            getTransactions().addAll(transactions);
        }
        return account;
    }

    private List<Transaction> createTransactions(List<Event> events, String id) {
        List<Transaction> list = new ArrayList<>();
        for (Event event : events) {
            Transaction transaction = createTransaction(event, id);
            list.add(transaction);
        }
        return list;
    }

    private Transaction createTransaction(Event event, String id) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setToAccountId(id);
        transaction.setAmount((Double) event.getProperty("Сума у валюті картки"));
        transaction.setExchangeRate(1.0F);
        transaction.setDate((String) event.getProperty("Дата"));
        transaction.setFromAccountId("6ca510bd-28ca-45a1-93ab-e45797d2832e");
        String time = (String) event.getProperty("Час");
        transaction.setDescription(event.getProperty("Опис операції") + " " + time);
        return transaction;
    }

    private Account createAccount(String category) {
        Account account = new Account();
        account.setId(UUID.randomUUID().toString());
        account.setName(category);
        account.setAccountDirection(EXPENSE);
        account.setColor(-8119082);
        account.setCurrencyId("default");
        account.setDescription("");
        return account;
    }

//    static class AccountDirection {
//    private final int INCOME = 1;
    static final int EXPENSE = 2;
//    private final int IN_WALLET = 3;
//    }

    public Account findAccountByCategory(String category, Account account) {
        if (!isParent(account)) {
            return null;
        }
        List<Account> items = account.getItems();
        for (Account item : items) {
            if (item.getName().contains(category)) {
                return item;
            } else {
                Account account1 = findAccountByCategory(category, item);
                if (account1 == null){
                    continue;
                } else {
                    return account1;
                }
            }
        }
        return null;
    }

    public Account findAccountByCategory(String category) {
        return findAccountByCategory(category, this.account);
    }

    public Account createAccount(Event event) {
        Account account = findAccountByCategory(event.getCategory());
        if (account == null) {
            account = new Account();
            account.setId(UUID.randomUUID().toString());
            account.setName(event.getCategory());
            account.setAccountDirection(EXPENSE);
            account.setCurrencyId("default");
            account.setDescription("");
        }
        return account;
    }
}