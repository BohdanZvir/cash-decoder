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

        return p24.stream()
                .map(event -> {
                    String p24Category = event.getCategory();
                    String cashCategory = mapCategoryToCash(p24Category);
                    event.setCategory(cashCategory);
                    return event;
                })
                .collect(Collectors.toList());
    }

    public String mapCategoryToCash(String category) {
        String result = category;
        if (category != null && categoryMap.containsKey(category)) {
            result = categoryMap.get(category);
        } else {
            if (category == null) {
                category = "null";
            }
            categoryMap.put(category, category);
            addCategoryToCsv(category);
        }
        return result;
    }


    public static Map<String, List<Event>> groupByCategory(List<Event> events) {
        return events.stream().collect(Collectors.groupingBy(Event::getCategory));
    }

    public void addCategoryToCsv(String newCategory) {
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
