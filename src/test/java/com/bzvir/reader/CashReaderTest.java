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

        Account item1 = new Account();
        item1.setAccountDirection(2);
        item1.setColor(-8119082);
        item1.setCurrencyId("default");
        item1.setDescription("");
        String accountId_1 = "6ca510bd-28ca-45a1-93ab-e45797d2832e";
        item1.setId(accountId_1);
        item1.setName("@string/leisure_activities");
        item1.setItems(new ArrayList<>());

        Account item2 = new Account();
        item2.setAccountDirection(2);
        item2.setColor(-10854908);
        item2.setCurrencyId("default");
        item2.setDescription("");
        String accountId_2 = "924e2c6d-68d7-49c7-990f-dd02e17fb30e";
        item2.setId(accountId_2);
        item2.setName("@string/recreation");
        item2.setItems(new ArrayList<>());

        Account account = new Account();
        account.setAccountDirection(2);
        account.setColor(-13647313);
        account.setCurrencyId("default");
        account.setDescription("");
        account.setId("af86d9f7-b4bc-41f1-89a5-0bd8cd436ed3");
        account.setName("@string/entertainment");
        account.setItems(Arrays.asList(item1, item2));

        Transaction trans1 = new Transaction();
        trans1.setAmount(20.0);
        trans1.setExchangeRate(1.0F);
        trans1.setDate("2014-09-13");
        trans1.setDescription("ratush");
        trans1.setFromAccountId(accountId_1);
        trans1.setId("a7c96291-044a-4ebc-a3b3-9fd424b56952");
        trans1.setToAccountId("13f0d705-d997-449b-9994-0fbc546f6e1e");

        Transaction trans2 = new Transaction();
        trans2.setAmount(15.0);
        trans2.setExchangeRate(1.0F);
        trans2.setDate("2015-01-04");
        trans2.setDescription("lecture");
        trans2.setFromAccountId(accountId_1);
        trans2.setId("61c7cf89-8369-4ab5-9e8a-abd3a86338be");
        trans2.setToAccountId("13f0d705-d997-449b-9994-0fbc546f6e1e");

        Transaction trans3 = new Transaction();
        trans3.setAmount(30.0);
        trans3.setExchangeRate(1.0F);
        trans3.setDate("2014-09-13");
        trans3.setDescription("palace");
        trans3.setFromAccountId(accountId_1);
        trans3.setId("ba3f976f-84ef-4430-9822-e7ba0691bfd6");
        trans3.setToAccountId("13f0d705-d997-449b-9994-0fbc546f6e1e");

        Transaction trans4 = new Transaction();
        trans4.setAmount(60.0);
        trans4.setExchangeRate(1.0F);
        trans4.setDate("2014-07-06");
        trans4.setDescription("cinema");
        trans4.setFromAccountId(accountId_2);
        trans4.setId("22311e13-e7ed-47e5-84d2-3639a96bfe62");
        trans4.setToAccountId("13f0d705-d997-449b-9994-0fbc546f6e1e");

        CashReader reader = spy(new CashReader(SAMPLE_DIR));
        doReturn(Arrays.asList(trans1, trans2, trans3, trans4)).when(reader).getTransactions();

        List<Account> accounts = Collections.singletonList(account);
        List<Event> events = reader.aggregateEvents(accounts, new LinkedList<>());

        assertThat(events, hasSize(4));
    }

}