package com.bzvir.util;

import com.bzvir.model.Event;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bohdan.
 */
public class EventJoiner {


    private Map<String, String> categoryMap;

    public EventJoiner(){
        categoryMap = new HashMap<>();
        categoryMap.put("Перекази", "transfers");
        categoryMap.put("Оренда", "rent");
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
