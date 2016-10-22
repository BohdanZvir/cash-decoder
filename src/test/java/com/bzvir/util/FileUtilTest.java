package com.bzvir.util;

import com.burtyka.cash.core.Account;
import com.bzvir.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Created by bohdan.
 */
public class FileUtilTest extends AbstractTest {

    private FileUtil util;

    @Before
    public void setUp() {
        util = new FileUtil();
    }

    @Test
    public void writeObject() {
        String stub = SAMPLE_DIR + "account2.dat";

        util.writeObject(new Account(), stub);

        File accountFile = new File(stub);
        assertTrue(accountFile.exists());
        assertTrue(stub + " should be deleted", accountFile.delete());
    }
}