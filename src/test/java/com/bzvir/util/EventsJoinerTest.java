package com.bzvir.util;

import com.bzvir.model.Event;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.bzvir.util.EventJoiner.groupByCategory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
        Event cash = dummyCashEvent("@string/leisure_activities", "");
        Event privat24 = dummyPrivat24Event("Перекази", "privat24");

        List<Event> list = eventJoiner.join(toList(cash), toList(privat24));

        assertThat(list, hasItem(cash));
        assertThat(list, hasItem(privat24));
    }

    @Test
    public void joinTwoEventsWithChangingCategory() {
        Event cash = dummyCashEvent("@string/leisure_activities", "cash");
        Event privat24 = dummyPrivat24Event("Перекази", "");

        List<Event> list = eventJoiner.join(toList(cash), toList(privat24));
        Event newPrivat24 = dummyPrivat24Event("transfers", "");

        assertThat(list, hasItem(cash));
        assertThat(list, hasItem(newPrivat24));
    }

    @Test
    public void joinEventsWithTwoTimesChangingCategoory() {
        Event cash = dummyCashEvent("@string/leisure_activities", "cash");
        Event privat24_1 = dummyPrivat24Event("Перекази", "1");
        Event privat24_2 = dummyPrivat24Event("Оренда", "2");

        List<Event> list = eventJoiner.join(toList(cash), toList(privat24_1, privat24_2));
        Event newPrivat24_1 = dummyPrivat24Event("transfers", "1");
        Event newPrivat24_2 = dummyPrivat24Event("rent", "2");

        assertThat(list, hasItem(cash));
        assertThat(list, hasItem(newPrivat24_1));
        assertThat(list, hasItem(newPrivat24_2));
    }

    @Test
    public void groupingEventsByCategoriesWithTwoDistinct() {
        String category1 = "cat_1";
        String category2 = "cat_2";
        Event p1_1 = dummyPrivat24Event(category1, "1");
        Event p1_2 = dummyPrivat24Event(category1, "2");
        Event p2_1 = dummyPrivat24Event(category2, "1");

        Map<String, List<Event>> result = groupByCategory(Arrays.asList(p1_1, p1_2, p2_1));

        assertThat(result.keySet(), hasItem(category1));
        assertThat(result.keySet(), hasItem(category2));
        List<Event> events1 = result.get(category1);
        assertThat(events1, hasItem(p1_1));
        assertThat(events1, hasItem(p1_2));
        List<Event> events2 = result.get(category2);
        assertThat(events2, hasItem(p2_1));
    }

    @Test
    public void groupingTwoEventsByCategoriesWithOneDistinct() {
        String category = "cat";
        Event p1_1 = dummyPrivat24Event(category, "1");
        Event p1_2 = dummyPrivat24Event(category, "2");

        Map<String, List<Event>> result = groupByCategory(Arrays.asList(p1_1, p1_2));

        assertThat(result.keySet(), hasItem(category));
        assertThat(result.keySet(), hasSize(1));
        List<Event> events1 = result.get(category);
        assertThat(events1, hasItem(p1_1));
        assertThat(events1, hasItem(p1_2));
    }
}
