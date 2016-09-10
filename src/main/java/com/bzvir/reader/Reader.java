package com.bzvir.reader;

import com.bzvir.model.Event;

import java.util.List;
import java.util.Set;

/**
 * Created by bohdan.
 */
public interface Reader {

    Set<String> getTitles();
    boolean checkTitlesOnPresence();
    List<Event> loadData();
    void reverseConvert(List<Event> p24);
}
