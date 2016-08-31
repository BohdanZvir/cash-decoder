package com.bzvir.reader;

import com.bzvir.model.Category;
import com.bzvir.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bohdan.
 */
public interface Reader {

    Map<Category,Double> collectByCategories(LocalDate timeStart, LocalDate timeEnd);
    Set<String> getTitles();
    boolean checkTitlesOnPresence();
    List<Event> loadData();
}
