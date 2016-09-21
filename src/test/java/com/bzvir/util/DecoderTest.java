package com.bzvir.util;

import com.bzvir.model.Event;
import org.junit.Before;
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
//
//    @Test
//    public void categoryChangedForTwoP24EventSkippedForCash() {
//        Event cash = dummyCashEvent("@string/leisure_activities", "cash");
//        Event p24_1 = dummyPrivat24Event("Перекази", "1");
//        Event p24_2 = dummyPrivat24Event("Оренда", "2");
//
//        List<Event> list = decoder.saveToCash(toList(p24_1, cash, p24_2));
//        Event cashP24_1 = dummyPrivat24Event("transfers", "1");
//        Event cashP24_2 = dummyPrivat24Event("rent", "2");
//
//        assertThat(list, hasItem(cash));
//        assertThat(list, hasItem(cashP24_1));
//        assertThat(list, hasItem(cashP24_2));
//    }
}