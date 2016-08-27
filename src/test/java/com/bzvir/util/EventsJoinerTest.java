package com.bzvir.util;

import com.bzvir.model.Event;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Created by bohdan.
 */
public class EventsJoinerTest extends AbstractTest {

    private EventJoiner eventJoiner;

    @Before
    public void setUp() {
        eventJoiner = new EventJoiner();
    }

    @Test
    public void joinOneCashAndOnePrivat24Event() {
        Event cash = createCashEvent("@string/leisure_activities", "2014-09.05", "");
        Event privat24 = createPrivat24Event("Перекази", "01.07.2016", "19:39", "Переказ зі своєї карти 51**82 через додаток Приват24");

        Set<Event> list = eventJoiner.join(Arrays.asList(cash), Arrays.asList(privat24));

        assertThat(list, hasItem(cash));
        assertThat(list, hasItem(privat24));
    }

    @Test
    public void joinTwoEventsWithChangingCategoory() {
        Event cash = createCashEvent("@string/leisure_activities", "2014-09.05", "");
        Event privat24 = createPrivat24Event("Перекази", "01.07.2016", "19:39", "Переказ зі своєї карти 51**82 через додаток Приват24");

        Set<Event> list = eventJoiner.join(Arrays.asList(cash), Arrays.asList(privat24));
        Event newPrivat24 = createPrivat24Event("transfers", "01.07.2016", "19:39", "Переказ зі своєї карти 51**82 через додаток Приват24");

        assertThat(list, hasItem(newPrivat24));
    }
}
