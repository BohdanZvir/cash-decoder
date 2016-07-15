package com.bzvir.test;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import org.abstractmeta.toolbox.compilation.compiler.JavaSourceCompiler;
import org.abstractmeta.toolbox.compilation.compiler.impl.JavaSourceCompilerImpl;
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

    public String readClassDeclarations(List<String> args) {
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
        String[] lines = textLine.split(System.getProperty("line.separator"));
        String delimiter = "////";
        String cleaned = cleanDeclarations(lines, delimiter);
        return divideByDelimiter(delimiter, cleaned);
    }

    private String cleanDeclarations(String[] lines, String delimiter) {
        String packageName = extractPackageName(lines[0].split(" ")[1]);

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < lines.length; i++) {

            if (lines[i].startsWith("////")) {
                continue;
            }else if (lines[i].length() < 1) {
            } else if (lines[i].startsWith("class")) {
                sb.append(delimiter)
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

    public List<String> constructClasses(String sampleFile, File pathToSave) {
        List<String> classes = generateClassDeclarations(sampleFile);
        return classes.stream()
                .map((s -> constructClass(s, pathToSave)))
                .filter(s -> s.isEmpty())
                .collect(Collectors.toList());
    }

    private String constructClass(String sourceCode, File pathToSave) {
        String packageName = extractWord(sourceCode, "package", ';');
        String className = extractWord(sourceCode, "class", ' ');

        String packagePath = packageName.replaceAll("\\.", File.separator);
        File file = new File(pathToSave, packagePath);
        file.mkdirs();
        try {
            Files.write(sourceCode, new File(file, className + ".java"), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return Joiner.on(".").join(packageName, className, "java");
    }

    public List<String> convertCanonicalClassNames(String path, List<String> canonicalName) {
        return canonicalName.stream()
                    .map(s -> s.replace(".", path + File.separator + ".java"))
                    .filter(s -> new File(s).exists())
                    .collect(Collectors.toList());
    }
}
