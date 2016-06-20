package com.bzvir.test;

import com.bzvir.reader.PrivatXlsReader;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by bohdan on 18.06.16.
 */
public class DecoderTest {
    private final String SAMPLE_DIR = System.getProperty("user.dir") + "/sample data/";

    @Test
    public void readPrivat24Report() {
        com.bzvir.reader.Reader reader = new PrivatXlsReader();
        String filePath = SAMPLE_DIR + "statements.xls";
        Map<String , String> result = reader.readFile(filePath);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

}