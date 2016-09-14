package com.bzvir.util;

import com.bzvir.model.Event;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.bzvir.util.EventMapper.groupByCategory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Created by bohdan.
 */
public class EventsMapperTest extends AbstractTest {

    private EventMapper mapper;

    @Before
    public void setUp() {
        mapper = new EventMapper();
    }

    @Test
    public void categoryChangedForP24Event() {
        Event p24 = dummyPrivat24Event("Перекази", "");

        List<Event> list = mapper.mapToCashCategories(toList(p24));
        Event cash = dummyPrivat24Event("transfers", "");

        assertThat(list, hasItem(cash));
    }

    @Test
    public void categoryChangedForTwoP24EventSkippedForCash() {
        Event cash = dummyCashEvent("@string/leisure_activities", "cash");
        Event p24_1 = dummyPrivat24Event("Перекази", "1");
        Event p24_2 = dummyPrivat24Event("Оренда", "2");

        List<Event> list = mapper.mapToCashCategories(toList(p24_1, cash, p24_2));
        Event cashP24_1 = dummyPrivat24Event("transfers", "1");
        Event cashP24_2 = dummyPrivat24Event("rent", "2");

        assertThat(list, hasItem(cash));
        assertThat(list, hasItem(cashP24_1));
        assertThat(list, hasItem(cashP24_2));
    }

    @Test
    public void threeEventsWithTwoDistinctCategoriesGrouped() {
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
    public void twoEventsWithSameCategoryGrouped() {
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
