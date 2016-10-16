package com.bzvir.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by root.
 */
public class Privat24ResourceUtil {

    private ResourceBundle bundle;

    public Privat24ResourceUtil(String resourceName) {
        bundle = ResourceBundle.getBundle(resourceName);
    }

    public List<String> readProperties() {
        List<String> titles = new ArrayList<>();
        int i = 0;
        while (bundle.containsKey("p24.title." + i)) {
            String title = bundle.getString("p24.title." + i);
            titles.add(toUTF8(title));
            i++;
        }
        return titles;
    }

    private String toUTF8(String title) {
        byte ptext[] = title.getBytes(ISO_8859_1);
        return new String(ptext, UTF_8);
    }

}
