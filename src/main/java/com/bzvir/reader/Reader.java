package com.bzvir.reader;

import com.bzvir.model.Category;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
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
    Set<String> readRowTitles(Sheet sheet);
}
