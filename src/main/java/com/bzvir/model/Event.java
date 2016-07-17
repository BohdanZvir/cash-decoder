package com.bzvir.model;

import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by bohdan on 17.07.16.
 */
@ToString
public class Event {
    private Map<String, Object> data;

    public Event() {
        data = new LinkedHashMap<>();
    }

    public void setProperty(String key, Object value) {
        data.put(key, value);
    }

    public Object getProperty(String key) {
        return data.getOrDefault(key, new Object());
    }
}
