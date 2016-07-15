package com.bzvir.test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.unsynchronized.jdeserialize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        System.out.println(textLine);
        if (textLine == null || textLine.isEmpty()) {
            throw new RuntimeException("Had not captured jdeserialize output");
        }
        return textLine;
    }

    public List<String> getClassDeclaration(String textLine) {
        String cleaned = cleanDeclarations(textLine);
        return divideByDelimiter("", cleaned);
    }

    public List<String> extractOnlyDeclarations(String text) {
        String[] strings = text.split(System.getProperty("line.separator"));
        List<String> collect = Arrays.stream(strings)
                .filter(s -> !s.startsWith("read"))
                .filter(s -> !s.startsWith("////"))
                .collect(Collectors.toList());
        return collect;
    }

    public String cleanDeclarations(String text) {
        text = text.split("////")[1];
        List<String> dec = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String[] lines = text.split("[\n|\\s]+class\\s+");
        for (int i = 1; i < lines.length; i++) {

            if (lines[i].startsWith("////")) {
                continue;
            }else if (lines[i].length() < 1) {
            } else if (lines[i].startsWith("class")) {


                String canonicalName = lines[i].split(" ")[1];
                int pointIndex = canonicalName.lastIndexOf('.');
                String packageName = canonicalName.substring(0, pointIndex - 1);
                String className = canonicalName.substring(pointIndex);

                sb
                        .append("package ").append(packageName).append(";\n\n")
                        .append("@lombok.Getter\n@lombok.Setter\n@lombok.ToString\n")
                        .append("public ").append(lines[i].replaceFirst(packageName + ".", ""));
            } else if (lines[i].length() > 1) {
                sb.append(lines[i]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String extractPackageName(String s) {
        String packageLine = (s.contains(" ")) ? s.split(" ")[1] : s;
        return packageLine.substring(0, packageLine.lastIndexOf("."));
    }

    private List<String> divideByDelimiter(String delimiter, String text) {
        return Arrays.asList(text.split(delimiter))
                .stream().filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public static String extractWord(String text, String beforeWord, char splitter) {
        int wordIndex = (!beforeWord.isEmpty())
                ? text.lastIndexOf(beforeWord) + beforeWord.length() + 1
                : 0;
        int splitterIndex = text.indexOf(splitter, wordIndex);
        return text.substring(wordIndex, splitterIndex);
    }

    public List<String> generateClassDeclarations(String dataFilePath) {
        String dirtyClassDeclarations = readClassDeclarations(dataFilePath);
        return getClassDeclaration(dirtyClassDeclarations);
    }

    public List<File> constructClasses(String sampleFile, File pathToSave) {
        List<String> classes = generateClassDeclarations(sampleFile);
        return classes.stream()
                .map((s -> constructClass(s, pathToSave)))
                .filter(s -> s != null)
                .collect(Collectors.toList());
    }

    private File constructClass(String sourceCode, File pathToSave) {
        String packageName = extractWord(sourceCode, "package", ';');
        String className = extractWord(sourceCode, "class", ' ');

        String packagePath = packageName.replaceAll("\\.", File.separator);
        File file = new File(pathToSave, packagePath);
        file.mkdirs();
        File created = new File(file, className + ".java");
        try {
            Files.write(sourceCode, created, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        Joiner.on(".").join(packageName, className, "java");
        return created;
    }

}
