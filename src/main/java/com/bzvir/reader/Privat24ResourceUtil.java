package com.bzvir.reader;

import com.google.common.collect.Lists;

import java.util.*;

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
        Map<String, String> titles = new HashMap<>();
        int i = 0;
        while (bundle.containsKey(i + ".title.p24")) {
            String p24 = bundle.getString(i + ".title.p24");
            String event =
                    (bundle.containsKey(i + ".title.event"))
                            ? bundle.getString(i + ".title.event")
                            : "";
            titles.put(toUTF8(p24), event);
            i++;
        }
        return Lists.newArrayList(titles.keySet());
    }

    private String toUTF8(String title) {
        byte ptext[] = title.getBytes(ISO_8859_1);
        return new String(ptext, UTF_8);
    }

}
