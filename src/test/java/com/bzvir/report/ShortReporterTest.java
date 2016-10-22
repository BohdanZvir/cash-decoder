package com.bzvir.report;

import com.burtyka.cash.core.Account;
import com.burtyka.cash.core.TransactionManager;
import com.bzvir.AbstractTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;

/**
 * Created by bohdan.
 */
public class ShortReporterTest extends AbstractTest {

    private static ShortReporter reporter;
    @Mock
    private ShortReporter reporterMocked;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @BeforeClass
    public static void beforeClass() {
        reporter = constructShortReporter();
    }

    @Test
    public void doReport() throws Exception {
        String actual = reporter.doReport();

        assertThat(actual, not(isEmptyString()));
    }

    @Test
    public void failsIfAccountItemsIsEmpty() {
        Account account = Mockito.mock(Account.class);
        TransactionManager tm = Mockito.mock(TransactionManager.class);

        when(account.getItems()).thenReturn(new ArrayList<>());

        String report = new ShortReporter(account).doReport();
        assertThat(report, isEmptyString());
    }

}