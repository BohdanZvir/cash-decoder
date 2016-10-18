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
    private final static String CATEGORY = "Category";
    public static final String DATE = "Date";
    public static final String AMOUNT = "Amount";
    public static final String DESCRIPTION = "Description";
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

    public void setDate(String date) {
        data.put(DATE, date);
    }

    public String getDate() {
        return data.containsKey(DATE) ? (String) data.get(DATE) : "";
    }

    public void setAmount(int amount) {
        data.put(AMOUNT, amount);
    }

    public int getAmount() {
        return data.containsKey(AMOUNT) ? (int) data.get(AMOUNT) : 0;
    }

    public void setDescription(String description) {
        data.put(DESCRIPTION, description);
    }

    public String getDescription() {
        return data.containsKey(DESCRIPTION) ? (String) data.get(DESCRIPTION) : "";
    }

    public void setProperty(String key, Object value) {
        data.put(key, value);
    }

    public Object getProperty(String key) {
        return data.getOrDefault(key, new Object());
    }
}
