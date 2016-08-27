package com.bzvir.util;

import com.bzvir.model.Event;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by bohdan.
 */
public class EventJoiner {


    public Set<Event> join(List<Event> cash, List<Event> privat24) {
        Set<Event> result = new LinkedHashSet<>(cash);
        result.addAll(mapCategory(privat24));
        return result;
    }

    private Collection<? extends Event> mapCategory(Collection<Event> privat24) {

        return privat24.stream().map(event -> {
            Object category = event.getProperty("Категорія");
            if (category != null && category.equals("Перекази")) {
                event.setProperty("Категорія", "transfers");
            }
            return event;
        }).collect(Collectors.toList());
    }
}
