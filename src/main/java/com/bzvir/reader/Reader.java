package com.bzvir.reader;

import java.util.Map;

/**
 * Created by bohdan on 20.06.16.
 */
public interface Reader {
    Map<String, String> readFile(String filePath);
}
