package com.bzvir.test;

import org.abstractmeta.toolbox.compilation.compiler.JavaSourceCompiler;
import org.abstractmeta.toolbox.compilation.compiler.impl.JavaSourceCompilerImpl;
import org.unsynchronized.jdeserialize;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by bohdan on 19.06.16.
 */
public class ClassGenerator {

    public ClassGenerator(){
        super();
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
        String packageName = extractPackageName(lines[0]);

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

    public String extractPackageName(String line) {
        String packageLine = line.split(" ")[1];
        return packageLine.substring(0, packageLine.lastIndexOf("."));
    }

    public Class loadClass(String sourceCode, String className) {
        JavaSourceCompiler javaSourceCompiler = new JavaSourceCompilerImpl();
        JavaSourceCompiler.CompilationUnit compilationUnit = javaSourceCompiler.createCompilationUnit();
        compilationUnit.addJavaSource(className, sourceCode);
        ClassLoader classLoader = javaSourceCompiler.compile(compilationUnit);
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
}
