package com.bzvir.util;

import java.io.*;

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

    public void writeObject(Object obj, String filePath) {
        try (FileOutputStream file = new FileOutputStream(filePath);
             ObjectOutputStream output = new ObjectOutputStream(file)) {

            output.writeObject(obj);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
