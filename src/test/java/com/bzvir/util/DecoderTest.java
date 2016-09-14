package com.bzvir.util;

import com.bzvir.model.Event;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by bohdan.
 */
public class DecoderTest extends AbstractTest {

    @Test
    public void readP24Events() {
        Decoder decoder = new Decoder();
        List<Event> p24 = decoder.readP24();
        assertThat(p24, not(empty()));
    }

    @Test
    public void readCashEvents() {
        Decoder decoder = new Decoder();
        List<Event> cash = decoder.readCash();
        assertThat(cash, not(empty()));
    }

    @Test
    public void joinCashAndPrivat24Events() {
        Decoder decoder = new Decoder();
        List<Event> p24 = decoder.readP24();
        List<Event> cash = decoder.readCash();
        EventJoiner joiner = new EventJoiner();
        List<Event> events = joiner.join(cash, p24);

        int expected = cash.size() + p24.size();

        assertThat(events.size(), equalTo(expected));
    }

    @Test
    public void updateCash() {
        Decoder decoder = new Decoder();
        List<Event> p24 = decoder.readP24();
        List<Event> cash = decoder.readCash();
        EventJoiner joiner = new EventJoiner();
        List<Event> events = joiner.join(cash, p24);

        decoder.saveToCash(events);
    }
}