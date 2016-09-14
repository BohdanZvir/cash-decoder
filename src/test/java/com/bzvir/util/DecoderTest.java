package com.bzvir.util;

import com.bzvir.model.Event;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by bohdan.
 */
public class DecoderTest extends AbstractTest {

    private Decoder decoder;

    @Before
    public void setup() {
        decoder = new Decoder();
    }

    @Test
    public void readP24Events() {
        List<Event> p24 = decoder.readP24();
        assertThat(p24, not(empty()));
    }

    @Test
    public void readCashEvents() {
        List<Event> cash = decoder.readCash();
        assertThat(cash, not(empty()));
    }

    @Test
    @Ignore
    public void updateCash() {
        List<Event> p24 = decoder.readP24();

        decoder.saveToCash(p24);
    }
}