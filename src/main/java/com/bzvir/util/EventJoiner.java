package com.bzvir.util;

import com.bzvir.model.Event;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bohdan.
 */
public class EventJoiner {

    public Set<Event> join(List<Event> cash, List<Event> privat24) {
        Set<Event> result = new LinkedHashSet<>(cash);
        result.addAll(privat24);
        return result;
    }
}
