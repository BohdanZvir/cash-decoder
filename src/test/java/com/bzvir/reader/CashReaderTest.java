package com.bzvir.reader;

import com.burtyka.cash.core.Account;
import com.burtyka.cash.core.Transaction;
import com.bzvir.model.Event;
import com.bzvir.util.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
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
    }

    @Test
    public void loadDataIntoEventList() {
        reader = new CashReader(SAMPLE_DIR);
        List<Event> events = reader.loadData();

        assertThat(events, hasSize(greaterThan(100)));
        System.out.println("size :: " + events.size());
    }

    @Test
    public void checkIsParentOnSimpleAccount() {
        boolean parent = CashReader.isParent(new Account());

        assertFalse(parent);
    }

    @Test
    public void checkIsParentOnAccountWithTwoChilds() {
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
    public void aggregateEventsFromParentAccount() {

        String childId_1 = "child_1";
        String childId_2 = "child_2";
        String parentId = "parent";

        Account child1 = createAccount(childId_1, "cat child 1");
        Account child2 = createAccount(childId_2, "cat child 2");

        Account parent = createAccount(parentId, "cat parent");
        parent.setItems(Arrays.asList(child1, child2));

        Transaction trans1 = createTransaction(childId_1, "1 child 1");
        Transaction trans2 = createTransaction(childId_1, "2 child 1");
        Transaction trans3 = createTransaction(childId_1, "3 child 1");
        Transaction trans4 = createTransaction(childId_2, "4 child 2");

        CashReader spyReader = spy(reader);
        doReturn(Arrays.asList(trans1, trans2, trans3, trans4))
                .when(spyReader).getTransactions();

        List<Account> accounts = Collections.singletonList(parent);
        List<Event> events = spyReader.aggregateEvents(accounts);

        verify(spyReader, times(2)).getTransactions();
        assertThat(events, hasSize(4));
    }

    @Test
    public void convertP24EventToCash() {
        String parentDesc = "parent";
        String parentCateg = "cat0";
        Event parent = createPrivat24Event(parentCateg, parentDesc);
        List<Event> p24 = Arrays.asList(parent);
        Account account = reader.reverseConvert(p24);

        assertThat(account.getAccountDirection(), is(2));
        assertThat(account.getColor(), is(-8119082));
        assertThat(account.getCurrencyId(), is("default"));
        assertThat(account.getDescription(), is(parentDesc));
        assertThat(account.getId(), is("Id"));  //"6ca510bd-28ca-45a1-93ab-e45797d2832e"
        assertThat(account.getName(), is(parentCateg)); //"@string/leisure_activities"
    }

    @Test
    public void convertThreeP24EventsToCash() {
        Event parent = createPrivat24Event("cat0", "parent");
        Event child1 = createPrivat24Event("cat1", "child1");
        Event child2 = createPrivat24Event("cat2", "child2");
        List<Event> p24 = Arrays.asList(parent, child1, child2);

        CashReader reader = new CashReader(SAMPLE_DIR);
        Account account = reader.reverseConvert(p24);

        assertThat(account.getItems(), hasSize(2));
    }

}