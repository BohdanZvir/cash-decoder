package com.bzvir.util;

import org.abstractmeta.toolbox.compilation.compiler.JavaSourceCompiler;
import org.abstractmeta.toolbox.compilation.compiler.impl.JavaSourceCompilerImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bohdan on 15.07.16.
 */
public class ClassLoaderUtil {

    public List<Class> loadClass(List<String> declarations) {
        return declarations.stream().map(s -> loadClass(s)).collect(Collectors.toList());
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return fooClass;
    }

    public String extractWord(String text, String beforeWord, char splitter) {
        int wordIndex = (!beforeWord.isEmpty())
                ? text.lastIndexOf(beforeWord) + beforeWord.length() + 1
                : 0;
        int splitterIndex = text.indexOf(splitter, wordIndex);
        return text.substring(wordIndex, splitterIndex);
    }

}
