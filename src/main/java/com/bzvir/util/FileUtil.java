package com.bzvir.util;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Created by bohdan.
 */
public class FileUtil {

    public Object readObject(String filePath) {
        try (FileInputStream in = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(in)) {

            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
