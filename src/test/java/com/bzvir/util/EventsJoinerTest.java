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
    public void joinTwoEventsWithChangingCategory() {
        Event cash = createCashEvent("@string/leisure_activities", "", "cash");
        Event privat24 = createPrivat24Event("Перекази", "", "", "privat24");

        Set<Event> list = eventJoiner.join(Arrays.asList(cash), Arrays.asList(privat24));
        Event newPrivat24 = createPrivat24Event("transfers", "", "", "privat24");

        assertThat(list, hasItem(cash));
        assertThat(list, hasItem(newPrivat24));
    }

    @Test
    public void joinEventsWithTwoTimesChangingCategoory() {
        Event cash = createCashEvent("@string/leisure_activities", "", "cash");
        Event privat24_1 = createPrivat24Event("Перекази", "", "", "privat24_1");
        Event privat24_2 = createPrivat24Event("Оренда", "", "", "privat24_2");

        Set<Event> list = eventJoiner.join(Arrays.asList(cash), Arrays.asList(privat24_1, privat24_2));
        Event newPrivat24_1 = createPrivat24Event("transfers", "", "", "privat24_1");
        Event newPrivat24_2 = createPrivat24Event("rent", "", "", "privat24_2");

        assertThat(list, hasItem(cash));
        assertThat(list, hasItem(newPrivat24_1));
        assertThat(list, hasItem(newPrivat24_2));
    }
}
