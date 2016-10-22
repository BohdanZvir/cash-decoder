package com.bzvir.generator;

import com.bzvir.util.ConsoleOutputCapturer;
import org.unsynchronized.jdeserialize;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bohdan.
 */
public class ClassGenerator {

    public ClassGenerator(){
        super();
    }

    private List<String> getJdeserializeArgs() {
        List<String> args = new ArrayList<>();
        args.add("-nocontent");
        args.add("-noinstances");
        args.add("-filter");
        args.add("java.util.*");
        return args;
    }

    public String readClassDeclarations(String dataFilePath) {
        if (dataFilePath == null || !(new File(dataFilePath).exists())) {
            throw new IllegalArgumentException();
        }
        List<String> args = getJdeserializeArgs();
        args.add(dataFilePath);
        return readClassDeclarations(args);
    }

    private String readClassDeclarations(List<String> args) {
        ConsoleOutputCapturer capturer = new ConsoleOutputCapturer();
        capturer.start(false); //not write into console
        jdeserialize.main(args.toArray(new String[]{}));
        String textLine = capturer.stop();
        if (textLine == null || textLine.isEmpty()) {
            throw new RuntimeException("Had not captured jdeserialize output");
        }
        return textLine;
    }

    public List<String> clearFromJdeserialization(String text) {
        String[] strings = text.split(System.getProperty("line.separator"));
        return Arrays.stream(strings)
                .filter(s -> !s.startsWith("read"))
                .filter(s -> !s.startsWith("////"))
                .collect(Collectors.toList());
    }

    public Map<File, List<String>> buildClassDeclarationsWithFiles(List<String> lines, File pathToSave) {
        Map<File, List<String>> collect = new HashMap<>();

        Iterator<String> iterator = lines.iterator();
        List<String> list = new ArrayList<>();
        while (iterator.hasNext()) {
            String str = iterator.next();

            if (str.startsWith("class")) {
                list = new ArrayList<>();

                String canonicalName = str.split(" ")[1];
                File file = new File(pathToSave, canonicalName.replace(".", File.separator) + ".java");
                String packageName = extractPackageName(canonicalName);

                String sb = "package " + packageName + ";\n\n" +
                        "@lombok.Getter\n" +
                        "@lombok.Setter\n" +
                        "@lombok.ToString\n" +
                        "@lombok.EqualsAndHashCode\n" +
                        "public " + str.replaceFirst(packageName + ".", "") + "\n";
                list.add(sb);
                collect.put(file, list);
            } else if (!list.isEmpty() && !str.isEmpty()) {
                list.add(str + "\n");
            }
        }
        return collect;
    }

    public String extractPackageName(String canonicalName) {
        return canonicalName.substring(0, canonicalName.lastIndexOf('.'));
    }

    public Set<File> constructClasses(String dataFilePath, File pathToSave) {
        String dirtyDeclar = readClassDeclarations(dataFilePath);
        List<String> lines = clearFromJdeserialization(dirtyDeclar);
        Map<File, List<String>> classes = buildClassDeclarationsWithFiles(lines, pathToSave);

        classes.entrySet().stream()
                .filter(s -> writeFile(s.getKey(), s.getValue()))
                .collect(Collectors.toList());
        return classes.keySet();
    }

    private boolean writeFile(File file, List<String> text) {
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try (FileWriter writer = new FileWriter(file)) {
            for(String str: text) {
                writer.write(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
