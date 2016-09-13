package com.bzvir.reader;

import com.burtyka.cash.core.Account;
import com.burtyka.cash.core.Transaction;
import com.bzvir.model.Event;
import com.bzvir.util.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static com.bzvir.reader.CashReader.CASH_ACCOUNT_ID;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by bohdan.
 */
public class CashReaderTest extends AbstractTest {

    private CashReader reader;

    @Before
    public void setUp() {
        reader = new CashReader(SAMPLE_DIR);
    }

    @Test
    public void constructEventFromAccountAndTransaction() {
        Account account = AccountBuilder.build();
        Transaction transaction = TransactionBuilder.build();

        Event event = reader.constructEvent(account, transaction);

        assertThat(event.getProperty("accountId"), is(account.getId()));
        assertThat(event.getProperty("Сума у валюті картки"), is(transaction.getAmount()));
    }

    @Test
    public void sizeLoadedEventsFromCashGreater100() {
        reader = new CashReader(SAMPLE_DIR);
        List<Event> events = reader.loadData();

        assertThat(events, hasSize(greaterThan(100)));
        System.out.println("size :: " + events.size());
    }

    @Test
    public void simpleAccountIsNotParent() {
        boolean parent = CashReader.isParent(new Account());

        assertFalse(parent);
    }

    @Test
    public void accountWithTwoChildItemsIsParent() {
        Account child_1 = new Account();
        Account child_2 = new Account();
        Account parent = new Account();
        parent.setItems(Arrays.asList(child_1, child_2));

        boolean isParent = CashReader.isParent(parent);

        assertTrue(isParent);
    }

    // parent account has two child accounts,
    // first child account has three transactions,
    // second child account has one transaction.
    @Test
    public void created4EventsFromParentAccontTwoChildsAndTransactions() {

        String childId_1 = "child_1";
        String childId_2 = "child_2";
        String parentId = "parent";

        Account child1 = createAccount(childId_1, "cat child 1");
        Account child2 = createAccount(childId_2, "cat child 2");

        Account parent = createAccount(parentId, "cat parent");
        parent.setItems(toList(child1, child2));

        Transaction trans1 = createTransaction(childId_1, "1 child 1");
        Transaction trans2 = createTransaction(childId_1, "2 child 1");
        Transaction trans3 = createTransaction(childId_1, "3 child 1");
        Transaction trans4 = createTransaction(childId_2, "4 child 2");

        CashReader spyReader = spy(reader);
        doReturn(toList(trans1, trans2, trans3, trans4))
                .when(spyReader).getTransactions();

        List<Account> accounts = Collections.singletonList(parent);
        List<Event> events = spyReader.aggregateEvents(accounts);

        verify(spyReader, times(2)).getTransactions();
        assertThat(events, hasSize(4));
    }

    @Test
    public void createAccountForNonExistingCategory() {
        String category = "cat0";

        Account account = reader.getAccount(category);

        assertThat(account.getAccountDirection(), is(CashReader.EXPENSE));
        assertThat(account.getCurrencyId(), is("default"));
        assertThat(account.getId(), not(isEmptyOrNullString()));
        assertThat(account.getName(), is(category));
    }

    @Test
    public void findExistingAccountByCategory() {
        String category = "medicine";
        Account expected = createAccount("id", category);
        Account parent = createAccount("ddd", "some cat");
        parent.setItems(toList(expected));

        CashReader spy = spy(reader);
        doReturn(spy.findAccountByCategory(category, parent)).when(spy).findAccountByCategory(category);

        Account actual = spy.getAccount(category);

        assertThat(actual, is(expected));
    }

    @Test
    public void validateCreatedTransactionFromEvent() {
        double amount = 10.0;
        String date = "2015-10-22";
        String time = "11:54";
        String description = "event desc";
        String accountId = "accountId";

        Event event = new Event();
        event.setCategory("cat");
        event.setProperty("Дата", date);
        event.setProperty("Час", time);
        event.setProperty("Сума у валюті картки", amount);
        event.setProperty("Опис операції", description);

        Transaction transaction = reader.createTransaction(event, accountId);

        assertThat(transaction.getAmount(), is(amount));
        assertThat(transaction.getExchangeRate(), is(1.0F));
        assertThat(transaction.getDate(), is(date));
        assertThat(transaction.getDescription(), is(time + " " + description));
        assertThat(transaction.getFromAccountId(), is(accountId));
        assertThat(transaction.getToAccountId(), is(CASH_ACCOUNT_ID));
    }

    @Test
    public void savedAccountWithNonExistingCategory() {
        String category = "1515151515";

        Account account = reader.getAccount(category);
        Account actual = reader.findAccountByCategory(category);
        assertThat(actual, is(account));
    }

    @Test
    public void validateCreatedAccountFromEvent() {
        String description = "event";
        String category = "cat0";
        Event event = createPrivat24Event(category, description);

        String accountId = "accountId";
        Account account = createAccount(accountId, category);

        CashReader spy = spy(reader);
        doReturn(account).when(spy).createAccount(category);

        List<Event> p24 = toList(event);
        spy.reverseConvert(p24);

        verify(spy).createAccount(category);
        verify(spy).getTransactions();
        verify(spy).createTransaction(event, accountId);
    }

    @Test
    public void accountSavedToFileSystem() {
        String category = "32323223";
        String stubAccount = SAMPLE_DIR + "account2.dat";

        File expectSaveTo = new File(stubAccount);
        assertFalse(stubAccount + " shouldn't exsist", expectSaveTo.exists());

        Account cat = createAccount("id", category);

        CashReader spy = spy(reader);
        doReturn(stubAccount).when(spy).getFilePath(Account.class);

        spy.writeToFile(cat);

        verify(spy).getFilePath(Account.class);

        assertTrue(expectSaveTo.exists());
        assertTrue(stubAccount + " should be deleted", expectSaveTo.delete());
    }
}