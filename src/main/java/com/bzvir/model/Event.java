package com.bzvir.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by bohdan.
 */
@ToString
@EqualsAndHashCode
public class Event {
    private final static String CATEGORY = "Категорія";
    private Map<String, Object> data;

    public Event() {
        data = new LinkedHashMap<>();
    }

    public void setCategory(String value) {
        data.put(CATEGORY, value);
    }

    public String getCategory() {
        return data.containsKey(CATEGORY) ? (String) data.get(CATEGORY) : "";
    }

    public void setProperty(String key, Object value) {
        data.put(key, value);
    }

    public Object getProperty(String key) {
        return data.getOrDefault(key, new Object());
    }
}
