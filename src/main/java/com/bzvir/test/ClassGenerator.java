package com.bzvir.test;

import org.abstractmeta.toolbox.compilation.compiler.JavaSourceCompiler;
import org.abstractmeta.toolbox.compilation.compiler.impl.JavaSourceCompilerImpl;
import org.unsynchronized.jdeserialize;

import java.util.ArrayList;
import java.util.List;

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

    public String getDirtyClassDeclaration(String dataFilePath) {
        List<String> args = getJdeserializeArgs();
        args.add(dataFilePath);
        return getDirtyClassDeclaration(args);
    }

    public String getDirtyClassDeclaration(List<String> args) {
        ConsoleOutputCapturer capturer = new ConsoleOutputCapturer();
        capturer.start(false);
        jdeserialize.main(args.toArray(new String[]{}));
        String textLine = capturer.stop();

        return fixClassDeclaration(textLine);
    }

    public String fixClassDeclaration(String textLine) {
        String[] lines = textLine.split(System.getProperty("line.separator"));

        String packageLine = lines[0].split(" ")[1];
        String packageName = packageLine.substring(0, packageLine.lastIndexOf("."));

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n");

        for (int i = 1; i < lines.length; i++) {

            if (lines[i].startsWith("////")) {
            } else if (lines[i].startsWith("class")) {
                sb.append("@lombok.Getter\n@lombok.Setter\n@lombok.ToString\n");
                sb.append(lines[i].replaceFirst(packageName + ".", ""));
            } else {
                sb.append(lines[i]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public Class loadClass(String sourceCode) {
        JavaSourceCompiler sourceCompiler = new JavaSourceCompilerImpl();
        JavaSourceCompiler.CompilationUnit compilationUnit = sourceCompiler.createCompilationUnit();

        String packageName = extractWord(sourceCode, "package", ';');
        String className = extractWord(sourceCode, "class", ' ');

        className = packageName + "." + className;

        compilationUnit.addJavaSource(className, sourceCode);
        ClassLoader classLoader = sourceCompiler.compile(compilationUnit);
        Class fooClass = null;
        try {
            fooClass = classLoader.loadClass(className);
//            for(Method method : fooClass.getDeclaredMethods()) {
//                System.out.println(method);
//            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return fooClass;
    }

    private String extractWord(String text, String before, char splitter) {
        int indexPackage = text.lastIndexOf(before) + before.length() + 1;
        int indexSemiColon = text.indexOf(splitter, indexPackage);
        return text.substring(indexPackage, indexSemiColon);
    }

    public Class generateClass(String dataFilePath) {
        String classDeclaration = getDirtyClassDeclaration(dataFilePath);
        return loadClass(classDeclaration);
    }
}
