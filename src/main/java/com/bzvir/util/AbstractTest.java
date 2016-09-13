package com.bzvir.util;

import com.burtyka.cash.core.Account;
import com.burtyka.cash.core.Transaction;
import com.bzvir.model.Event;
import com.bzvir.reader.CashReader;
import com.bzvir.report.ShortReporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bohdan.
 */
public class AbstractTest {

    protected final static String SAMPLE_DIR = System.getProperty("user.dir") + "/sample data/";

    protected static ShortReporter constructShortReporter() {
        Account account = new CashReader(SAMPLE_DIR).getValue(Account.class);
//        TransactionManager manager = Decoder.getValue(TransactionManager.class);
        return new ShortReporter(account);
    }

    protected static class AccountBuilder {

        private AccountBuilder() {}

        public static Account build() {
//            Account(accountDirection=2, color=, accountList=null, currencyId=default,
//              description=, id=6ca510bd-28ca-45a1-93ab-e45797d2832e, items=[],
//              name=@string/leisure_activities)
            Account account = new Account();
            account.setAccountDirection(2);
            account.setColor(-8119082);
            account.setCurrencyId("default");
            account.setDescription("");
            account.setId("6ca510bd-28ca-45a1-93ab-e45797d2832e");
            account.setItems(new ArrayList<>());
            account.setName("@string/leisure_activities");
            return account;
        }
    }

    protected static class TransactionBuilder {

        private TransactionBuilder() {}

        public static Transaction build() {
            Transaction transaction = new Transaction();
            transaction.setAmount(26.0);
            transaction.setExchangeRate(1.0F);
            transaction.setDate("2014-09.05");
            transaction.setFromAccountId("6ca510bd-28ca-45a1-93ab-e45797d2832e");
            transaction.setId("16251eef-710b-46f7-93bf-2177dd7ae4b8");
            transaction.setToAccountId("13f0d705-d997-449b-9994-0fbc546f6e1e");
            transaction.setDescription("ratush");
            return transaction;
        }
    }

    //create account with empty list of items and empty description
    protected Account dummyAccount(String id, String category) {
        Account account = new Account();
        account.setDescription("");
        account.setId(id);
        account.setName(category);
        account.setItems(new ArrayList<>());
        return account;
    }

    // create dummy transaction with stubbed id, amount, date, accountToId
    protected Transaction dummyTransaction(String childId_1, String description) {
        Transaction trans = new Transaction();
        trans.setId("id");
        trans.setAmount(12.0);
        trans.setDate("2014-08-20");
        trans.setDescription(description);
        trans.setFromAccountId(childId_1);
        trans.setToAccountId("13f0d705-d997-449b-9994-0fbc546f6e1e");
        return trans;
    }

    //create dummy Event with stybbed amount, currency
    protected Event dummyEvent(String category, String data, String time, String description) {
        Event event = new Event();
        event.setCategory(category);
        event.setProperty("Дата", data);
        event.setProperty("Час", time);
        event.setProperty("Сума у валюті картки", 10.0);
        event.setProperty("Опис операції", description);
        event.setProperty("Валюта картки", "default");
        return event;
    }

    protected Event dummyPrivat24Event(String category, String description) {
        return dummyEvent(category, "", "", "p24" + description);
    }

    protected Event dummyCashEvent(String category, String description) {
        Event cash = dummyEvent(category, "", "", "cash " + description);
        cash.setProperty("accountId", "6ca510bd-28ca-45a1-93ab-e45797d2832e");
        return cash;
    }

    protected <T> List<T> toList(T... objects){
        return Arrays.asList(objects);
    }
}
