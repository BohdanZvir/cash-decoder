package com.bzvir.reader;

import com.bzvir.model.Event;

import java.util.List;
import java.util.Set;

/**
 * Created by bohdan.
 */
public interface Reader {

    List<Event> loadData();
    void convertFromEvent(List<Event> p24);
    void saveToFileSystem();
}
