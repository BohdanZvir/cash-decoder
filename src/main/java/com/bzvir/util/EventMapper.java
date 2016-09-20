package com.bzvir.util;

import com.bzvir.model.Event;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bohdan.
 */
public class EventMapper {

    private FileUtil fileUtil;
    private Map<String, String> categoryMap;

    public EventMapper(){
        fileUtil = new FileUtil();
        categoryMap = loadCategoryMap();
    }

    private Map<String, String> loadCategoryMap() {
        String csvFile = getCsvFilePath();
        return fileUtil.readCategoryCsvFile(csvFile);
    }

    public List<Event> mapToCashCategories(List<Event> p24) {

        return p24.stream().map(event -> {
            String category = event.getCategory();
            if (category != null && categoryMap.containsKey(category)) {
                event.setCategory(categoryMap.get(category));
            } else {
                addCategoryToCsv(category);
            }
            return event;
        }).collect(Collectors.toList());
    }

    public static Map<String, List<Event>> groupByCategory(List<Event> events) {
        return events.stream().collect(Collectors.groupingBy(Event::getCategory));
    }

    public void addCategoryToCsv(String newCategory) {
        if (newCategory == null) {
            newCategory = "null";
        }
        String filePath = getCsvFilePath();
        String line = "\n" + newCategory+ "," + newCategory;
        fileUtil.appendLine(line, filePath);
    }

    private String getCsvFilePath() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL csvUrl = classLoader.getResource("category_mapping.csv");
        if (csvUrl == null || csvUrl.getFile() == null) {
            throw new RuntimeException("Bad path to category_mapping.csv");
        }
        return csvUrl.getFile();
    }
}
