package com.bzvir.test;

import org.unsynchronized.jdeserialize;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bohdan on 19.06.16.
 */
public class ClassGenerator {

    public ClassGenerator(){
        super();
    }

    public static List<String> getJdeserializeArgs() {
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
        boolean alsoWriteIntoConsole = false;
        capturer.start(alsoWriteIntoConsole);
        jdeserialize.main(args.toArray(new String[]{}));
        String textLine = capturer.stop();
        if (textLine == null || textLine.isEmpty()) {
            throw new RuntimeException("Had not captured jdeserialize output");
        }
        return textLine;
    }


    public List<String> clearDeclarations(String text) {
        String[] strings = text.split(System.getProperty("line.separator"));
        List<String> collect = Arrays.stream(strings)
                .filter(s -> !s.startsWith("read"))
                .filter(s -> !s.startsWith("////"))
                .collect(Collectors.toList());
        return collect;
    }

    public Map<File, List<String>> getClassDeclaration(String text, File pathToSave) {
        List<String> lines = clearDeclarations(text);
        Map<File, List<String>> collect = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        Iterator<String> iterator = lines.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();

            List<String> list = new ArrayList<>();
            if (str.startsWith("class")) {
                String canonicalName = str.split(" ")[1];
                File file = new File(pathToSave, canonicalName.replace(".", File.separator) + ".java");
                list = new ArrayList<>();
                sb = new StringBuilder();
                collect.put(file, list);
                int pointIndex = canonicalName.lastIndexOf('.');
                String packageName = canonicalName.substring(0, pointIndex - 1);
                sb
                        .append("package ").append(packageName).append(";\n\n")
                        .append("@lombok.Getter\n@lombok.Setter\n@lombok.ToString\n")
                        .append("public ").append(str.replaceFirst(packageName + ".", ""));
                list.add(sb.toString());
            }
            if (sb.length() != 0 && !str.isEmpty()) {
                list.add(str);
            }
        }
        return collect;
    }

    public String extractPackageName(String s) {
        String packageLine = (s.contains(" ")) ? s.split(" ")[1] : s;
        return packageLine.substring(0, packageLine.lastIndexOf("."));
    }

    public static String extractWord(String text, String beforeWord, char splitter) {
        int wordIndex = (!beforeWord.isEmpty())
                ? text.lastIndexOf(beforeWord) + beforeWord.length() + 1
                : 0;
        int splitterIndex = text.indexOf(splitter, wordIndex);
        return text.substring(wordIndex, splitterIndex);
    }

    public Map<File, List<String>> generateClassDeclarations(String dataFilePath, File pathToSave) {
        String dirtyClassDeclarations = readClassDeclarations(dataFilePath);
        return getClassDeclaration(dirtyClassDeclarations, pathToSave);
    }

    public Set<File> constructClasses(String sampleFile, File pathToSave) {
        Map<File, List<String>> classes = generateClassDeclarations(sampleFile, pathToSave);
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
