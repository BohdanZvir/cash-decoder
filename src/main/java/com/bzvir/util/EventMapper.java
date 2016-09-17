package com.bzvir.util;

import com.bzvir.model.Event;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bohdan.
 */
public class EventMapper {

    private Map<String, String> categoryMap;

    public EventMapper(){
        categoryMap = new HashMap<>();
        loadCategoryMap();
    }

    protected void loadCategoryMap() {

        String csvFile = getCsvFilePath();
        if (csvFile == null) {
            throw new RuntimeException("Bad path to category_mapping.csv");
        }
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()
                        && (line.contains("Cash") || line.contains("Privat24"))) {
                    continue;
                }
                String[] value = line.split(",");
                categoryMap.put(value[0], value[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        String csvFile = getCsvFilePath();
        if (csvFile == null) {
            throw new RuntimeException("Bad path to category_mapping.csv");
        }

        try(FileWriter fw = new FileWriter(csvFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.printf("%n%s,%s", newCategory, newCategory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCsvFilePath() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL csvUrl = classLoader.getResource("category_mapping.csv");
        if (csvUrl == null) {
            return null;
        }
        return csvUrl.getFile();
    }
}
