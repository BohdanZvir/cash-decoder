package com.bzvir.test;

import org.abstractmeta.toolbox.compilation.compiler.JavaSourceCompiler;
import org.abstractmeta.toolbox.compilation.compiler.impl.JavaSourceCompilerImpl;

import java.util.List;
import java.util.stream.Collectors;

import static com.bzvir.test.ClassGenerator.extractWord;

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
}
