package com.bzvir.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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

    public Map<String, String> readCategoryCsvFile(String csvFile) {
        Map<String, String> categoryMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()
                        || line.contains("Cash")
                        || line.contains("Privat24")) {
                    continue;
                }
                String[] value = line.split(",");
                categoryMap.put(value[0], value[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return categoryMap;
    }

    public void appendLine(String newLine, String csvFile) {
        try(FileWriter fw = new FileWriter(csvFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.printf(newLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
