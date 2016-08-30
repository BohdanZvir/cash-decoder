package com.bzvir.util;

import com.bzvir.model.Event;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bohdan.
 */
public class EventJoiner {

    private Map<String, String> categoryMap;

    public EventJoiner(){
        categoryMap = new HashMap<>();
        loadCategoryMap();
    }

    protected void loadCategoryMap() {

        ClassLoader classLoader = getClass().getClassLoader();
        URL csvUrl = classLoader.getResource("category_mapping.csv");
        if (csvUrl == null) {
            return;
        }
        String csvFile = csvUrl.getFile();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // pass headers
            while ((line = br.readLine()) != null) {
                String[] value = line.split(",");
                categoryMap.put(value[0], value[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<Event> join(List<Event> cash, List<Event> privat24) {
        Set<Event> result = new LinkedHashSet<>(cash);
        result.addAll(mapCategory(privat24));
        return result;
    }

    private Collection<? extends Event> mapCategory(Collection<Event> privat24) {

        return privat24.stream().map(event -> {
            Object category = event.getProperty("Категорія");
            if (category != null && categoryMap.containsKey(category)) {
                event.setProperty("Категорія", categoryMap.get(category));
            }
            return event;
        }).collect(Collectors.toList());
    }
}
