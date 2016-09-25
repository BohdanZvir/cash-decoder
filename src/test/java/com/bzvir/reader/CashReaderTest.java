package com.bzvir.reader;

import com.burtyka.cash.core.Account;
import com.burtyka.cash.core.Transaction;
import com.burtyka.cash.core.TransactionManager;
import com.bzvir.model.Event;
import com.bzvir.util.AbstractTest;
import com.bzvir.util.FileUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.bzvir.reader.CashReader.CASH_ACCOUNT_ID;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

/**
 * Created by bohdan.
 */
public class CashReaderTest extends AbstractTest {

    private CashReader reader;
    private FileUtil fileUtilMock;

    @Before
    public void setUp() {
        fileUtilMock = mock(FileUtil.class);

        when(fileUtilMock
                .readObject(SAMPLE_DIR + "account.dat"))
                .thenReturn(new Account());
        when(fileUtilMock
                .readObject(SAMPLE_DIR + "transactionManager.dat"))
                .thenReturn(new TransactionManager());
        doNothing()
                .when(fileUtilMock)
                .writeObject(any(), anyString());

        reader = new CashReader(SAMPLE_DIR, new FileUtil());
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
    public void sizeLoadedEventsFromCash() {
        Account expense = createRootAccountWithExpense();
        TransactionManager transactionManager = createTransactionalManagerForExpense();

        FileUtil fileUtil = mock(FileUtil.class);
        when(fileUtil.readObject(SAMPLE_DIR + "account.dat"))
                .thenReturn(expense);

        when(fileUtil.readObject(SAMPLE_DIR + "transactionManager.dat"))
                .thenReturn(transactionManager);

        reader = new CashReader(SAMPLE_DIR, fileUtil);
        List<Event> events = reader.loadData();

        assertThat(events, hasSize(2));
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

        Account child1 = dummyAccount(childId_1, "cat child 1");
        Account child2 = dummyAccount(childId_2, "cat child 2");

        Account parent = dummyAccount(parentId, "cat parent");
        parent.setItems(toList(child1, child2));

        Transaction trans1 = dummyTransaction(childId_1, "1 child 1");
        Transaction trans2 = dummyTransaction(childId_1, "2 child 1");
        Transaction trans3 = dummyTransaction(childId_1, "3 child 1");
        Transaction trans4 = dummyTransaction(childId_2, "4 child 2");

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
    public void accountWithExistingCategoryFound() {
        String category = "medicine";
        Account expected = dummyAccount("id", category);
        Account parent = dummyAccount("ddd", "some cat");
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
    public void accountAndTransactionCreatedFromEvent() {
        String description = "event";
        String category = "cat0";
        Event event = dummyPrivat24Event(category, description);

        String accountId = "accountId";
        Account account = dummyAccount(accountId, category);

        CashReader spy = spy(reader);
        doReturn(account).when(spy).createAccount(category);

        List<Event> p24 = toList(event);
        spy.reverseConvert(p24);

        verify(spy).createAccount(category);
        verify(spy).getTransactions();
        verify(spy).createTransaction(event, accountId);
        assertThat(spy.findTransactions(account), notNullValue());
    }

    @Test
    public void dummyAccountSavedToFileSystem() {
        Account dummy = dummyAccount("id", "32323223");
        CashReader reader = new CashReader(SAMPLE_DIR, fileUtilMock);

        reader.writeToFile(dummy);

        verify(fileUtilMock, times(2)).readObject(anyString());
        verify(fileUtilMock).writeObject(ArgumentMatchers.isA(Account.class), anyString());
    }

    @Test
    public void accountWithNewCategorySavedToExpensesAccount() {
        String category = "32323223";

        Account preCheck = reader.findAccountByCategory(category);
        assertThat(preCheck, nullValue());

        reader.getAccount(category);
        Account expense = reader.findAccountByCategory("expenses");

        boolean actual = false;
        for (Account account : expense.getItems()) {
            actual |= account.getName().contains(category);
        }
        assertTrue(actual);
    }

    @Test
    public void accountAndTransactionsSavedToFileSystem() {
       doNothing().when(fileUtilMock).writeObject(instanceOf(Account.class), "ha");

        CashReader spy = spy(new CashReader(SAMPLE_DIR, fileUtilMock));

        doReturn("ha").when(spy).getFilePath(Account.class);
        doReturn("ha").when(spy).getFilePath(TransactionManager.class);

        spy.saveToFileSystem();

        verify(spy, times(2)).writeToFile(any());
    }

    @Test
    public void categoryChangedForP24EventAndSkippedForCash() {
        String cashCategory = "@string/leisure_activities";
        String p24Category = "Перекази";
        Event cash = dummyCashEvent(cashCategory, " cash");
        Event p24_1 = dummyPrivat24Event(p24Category, " p24");

        CashReader spy = spy(reader);
        spy.reverseConvert(toList(p24_1, cash));
        doReturn("transfers").when(spy).mapCategory(p24Category);

        verify(spy).getAccount(cashCategory);
        verify(spy).findAccountByCategory(cashCategory);
        verify(spy).findAccountByCategory(p24Category);
        verify(spy).createAccount("transfers");
        verify(spy, never()).createAccount(cashCategory);
        verify(spy, never()).mapCategory(cashCategory);
    }
}