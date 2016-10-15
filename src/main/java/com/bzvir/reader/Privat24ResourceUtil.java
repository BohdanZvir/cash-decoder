package com.bzvir.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
            titles.add(title);
            i++;
        }
        return titles;
    }

}
