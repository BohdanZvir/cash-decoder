package com.bzvir.util;

import com.burtyka.cash.core.Account;
import com.burtyka.cash.core.TransactionManager;
import com.bzvir.reader.CashReader;
import com.bzvir.report.ShortReporter;

/**
 * Created by bohdan.
 */
public class AbstractTest {

    final static String SAMPLE_DIR = System.getProperty("user.dir") + "/sample data/";

    protected static ShortReporter constructShortReporter() {
        Account account = new CashReader(SAMPLE_DIR).getValue(Account.class);
//        TransactionManager manager = Decoder.getValue(TransactionManager.class);
        return new ShortReporter(account);
    }
}
