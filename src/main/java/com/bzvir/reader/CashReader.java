package com.bzvir.reader;

import com.burtyka.cash.core.*;
import com.bzvir.model.Event;
import com.bzvir.util.EventMapper;
import com.bzvir.util.FileUtil;

import java.util.*;
import java.util.stream.Collectors;

import static com.bzvir.util.EventMapper.groupByCategory;

/**
 * Created by bohdan.
 */
public class CashReader implements Reader {

    static final String CASH_ACCOUNT_ID = "13f0d705-d997-449b-9994-0fbc546f6e1e";
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
    private FileUtil fileUtil;

    public CashReader(String dirPath) {
        this.dirPath = dirPath;
        this.fileUtil = new FileUtil();
        init();
    }

    private void init() {
        account = getValue(Account.class);
        transactionManager = getValue(TransactionManager.class);
    }

    public <T> T getValue(Class<T> clazz) {
        String filePath = getFilePath(clazz);
        Object obj = fileUtil.readObject(filePath);
        if (obj == null) {
            throw new RuntimeException("Can't read: " + filePath);
        }
        return clazz.cast(obj);
    }

    public void saveToFileSystem() {
        writeToFile(this.account);
        writeToFile(this.transactionManager);
    }

    void writeToFile(Object root) {
        String filePath = getFilePath(root.getClass());
        fileUtil.writeObject(root, filePath);
    }

    String getFilePath(Class clazz) {
        String filename = files.get(clazz);
        return dirPath + filename;
    }

    @Override
    public List<Event> loadData() {
        Account expense = getExpenseAccount();

        if (hasData(expense)) {
            throw new RuntimeException("There are no items to work with.");
        }
        List<Account> items = expense.getItems();
        return aggregateEvents(items);
    }

    private boolean hasData(Account expense) {
        return expense == null
                || expense.getItems() == null
                || expense.getItems().isEmpty();
    }

    Event constructEvent(Account account, Transaction transaction) {
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
        if (hasData(this.account)) {
            throw new RuntimeException("There are no account to wort with.");
        }
        List<Account> items = account.getItems();
        for (Account item : items) {
            if (item.getName().contains("expenses")) {
                return item;
            }
        }
        throw new RuntimeException("Can't find Expenses account.");
    }

    List<Event> aggregateEvents(List<Account> items) {
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

    List<Event> findTransactions(Account item) {
        String id = item.getId();
        return getTransactions().stream()
                .filter(trans -> trans.getFromAccountId().equalsIgnoreCase(id))
                .map(trans ->  constructEvent(item, trans))
                .collect(Collectors.toList());
    }

    List<Transaction> getTransactions() {
        return transactionManager.getTransasctions();
    }

    @Override
    public void reverseConvert(List<Event> p24) {
        Map<String, List<Event>> grouped = groupByCategory(p24);

        for (Map.Entry<String, List<Event>> entry : grouped.entrySet()) {
            String category = entry.getKey();
            Account account = getAccount(category);

            List<Event> events = entry.getValue();
            List<Transaction> transactions = createTransactions(events, account.getId());
            saveTransactions(transactions);
        }
    }

    private List<Transaction> createTransactions(List<Event> events, String accountId) {
        return events.stream()
                .map(event -> createTransaction(event, accountId))
                .collect(Collectors.toList());
    }

    Transaction createTransaction(Event event, String fromAccountId) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setToAccountId(CASH_ACCOUNT_ID);

        transaction.setAmount((Double) event.getProperty("Сума у валюті картки"));
        transaction.setExchangeRate(1.0F);
        transaction.setDate((String) event.getProperty("Дата"));
        transaction.setFromAccountId(fromAccountId);
        String time = (String) event.getProperty("Час");
        transaction.setDescription(time  + " " + event.getProperty("Опис операції"));
        return transaction;
    }

    private void saveTransactions(List<Transaction> list) {
        if (list != null && !list.isEmpty()) {
            getTransactions().addAll(list);
        }
    }

    Account createAccount(String category) {
        Account account = new Account();
        account.setId(UUID.randomUUID().toString());
        account.setName(category);
        account.setAccountDirection(EXPENSE);
        account.setColor(-8119082);
        account.setCurrencyId("default");
        account.setDescription("");
        return account;
    }

//    AccountDirection: INCOME = 1; IN_WALLET = 3;
    static final int EXPENSE = 2;

    Account getAccount(String category) {
        Account account = findAccountByCategory(category);
        if (account == null) {
            String newCategory = new EventMapper().mapCategoryToCash(category);
            account = createAccount(category);
            updateExpenseAccount(account);
        }
        return account;
    }

    private void updateExpenseAccount(Account account) {
        Account expense = getExpenseAccount();
        List<Account> items = expense.getItems();
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(account);
    }

    Account findAccountByCategory(String category) {
        return findAccountByCategory(category, this.account);
    }

    Account findAccountByCategory(String category, Account root) {
        if (!isParent(root)) {
            return null;
        }
        List<Account> items = root.getItems();
        for (Account item : items) {
            if (item.getName().contains(category)) {
                return item;
            } else {
                Account account = findAccountByCategory(category, item);
                if (account != null){
                    return account;
                }
            }
        }
        return null;
    }
}