package com.bzvir.util;

import com.bzvir.model.Event;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.bzvir.util.CategoryManager.groupByCategory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.mockito.Mockito.*;

/**
 * Created by bohdan.
 */
public class CategoryManagerTest extends AbstractTest {

    private CategoryManager manager;

    @Before
    public void setUp() {
        manager = new CategoryManager();
    }

    @Test
    public void categoryChangedForP24Event() {
        String p24 = "Перекази";

        String actual = manager.mapCategoryToCash(p24);

        assertThat(actual, is("transfers"));
    }

    @Test
    public void anotherCategoryChangedForP24Event() {
        String p24 = "Оренда";

        String actual = manager.mapCategoryToCash(p24);

        assertThat(actual, is("rent"));
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

    @Test
    public void nonExistingCategoryAddedToCsvFile() {
        String newCategory = "newUnderExpense";
        CategoryManager spy = spy(manager);
        doNothing().when(spy).addCategoryToCsv(newCategory);

        spy.mapCategoryToCash(newCategory);

        verify(spy).addCategoryToCsv(newCategory);
    }
}
