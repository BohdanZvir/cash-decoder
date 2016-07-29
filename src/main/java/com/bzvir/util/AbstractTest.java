package com.bzvir.util;

import com.burtyka.cash.core.Account;
import com.burtyka.cash.core.TransactionManager;
import com.bzvir.report.ShortReporter;

/**
 * Created by bohdan on 29.07.16.
 */
public class AbstractTest {

    protected static ShortReporter constructShortReporter() {
        Account account = Decoder.getValue(Account.class);
        TransactionManager manager = Decoder.getValue(TransactionManager.class);
        return new ShortReporter(account, manager);
    }
}
