package com.bzvir.reader;

import com.bzvir.model.Category;
import com.bzvir.model.Event;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bohdan on 20.06.16.
 */
public interface Reader {

    Map<String, String> readFile(String filePath);
    Map<Category,Double> collectByCategories(LocalDate timeStart, LocalDate timeEnd);
    Sheet loadFirstSheet(String filePath);
    Set<String> getRowTitles();
    List<String> readRowTitles();
    boolean checkOnRowTitles();
    List<Event> loadData(Set<String> titles) throws Exception;
    Event constructEvent(Row row);
}
