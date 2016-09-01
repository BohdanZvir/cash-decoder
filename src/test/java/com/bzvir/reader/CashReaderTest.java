package com.bzvir.reader;

import com.burtyka.cash.core.Account;
import com.burtyka.cash.core.Transaction;
import com.bzvir.model.Event;
import com.bzvir.util.AbstractTest;
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

    @Test
    public void constructEventFromAccountAndTransaction() {
        CashReader reader = new CashReader(SAMPLE_DIR);
        Account account = AccountBuilder.build();
        Transaction transaction = TransactionBuilder.build();

        Event event = reader.constructEvent(account, transaction);

        assertThat(event.getProperty("accountId"), is(account.getId()));
    }

    @Test
    public void loadDataIntoEventList() {
        CashReader reader = new CashReader(SAMPLE_DIR);
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

        CashReader reader =
                spy(
                new CashReader(SAMPLE_DIR)
                )
                ;
        doReturn(Arrays.asList(trans1, trans2, trans3, trans4))
                .when(reader).getTransactions();

        List<Account> accounts = Collections.singletonList(parent);
        List<Event> events = reader.aggregateEvents(accounts);

        verify(reader, times(2)).getTransactions();
        assertThat(events, hasSize(4));
    }

}