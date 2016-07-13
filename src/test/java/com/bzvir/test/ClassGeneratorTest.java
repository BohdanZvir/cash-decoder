package com.bzvir.test;

/**
 * Created by bohdan on 19.06.16.
 */

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ClassGeneratorTest {

    private static String testDirPath = System.getProperty("user.dir") + "/sample data/";
    private ClassGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new ClassGenerator();
    }

    @Test
    public void printEmptyClassDeclaration() {

        List<String> args = ClassGenerator.getJdeserializeArgs();
        args.add(testDirPath + "transactionManager.dat");

        String textLine = generator.getDirtyClassDeclaration(args);
//        System.out.println(":: after capturing\n" + textLine);
        assertNotNull(textLine);
        assertFalse(textLine.isEmpty());
        assertTrue(textLine.contains("class"));
    }

    @Test
    public void generateFullClassDeclaration() {
        String str = "read: com.burtyka.cash.core.Account _h0x7e0003 = r_0x7e0000;  \n" +
                "//// BEGIN class declarations (excluding array classes) (exclusion filter java.util.*)\n" +
                "class com.burtyka.cash.core.Account implements java.io.Serializable {\n" +
                "    int accountDirection;\n" +
                "    int color;\n" +
                "    java.util.ArrayList accountList;\n" +
                "    java.lang.String currencyId;\n" +
                "    java.lang.String description;\n" +
                "    java.lang.String id;\n" +
                "    java.util.ArrayList items;\n" +
                "    java.lang.String name;\n" +
                "}\n" +
                "\n" +
                "//// END class declarations";

        String fixedDeclaration = generator.fixClassDeclaration(str);
        assertNotNull(fixedDeclaration);
        assertFalse(fixedDeclaration.contains("read:"));
        assertFalse(fixedDeclaration.contains("////"));
        int classIndex = fixedDeclaration.indexOf("class");
        int spaceIndex = fixedDeclaration.indexOf(' ', classIndex + 6);
        String className = fixedDeclaration.substring(classIndex + 6, spaceIndex);
        assertFalse(className.contains("."));
        assertTrue(Character.isUpperCase(className.charAt(0)));
    }

    @Test
    public void createClassFromText() {
        String sourceCode = "package com.burtyka.cash.core;\n" +
                "\n" +
                "@lombok.Getter\n" +
                "@lombok.Setter\n" +
                "@lombok.ToString\n" +
                "class Account2 implements java.io.Serializable {\n" +
                "    int accountDirection;\n" +
                "    int color;\n" +
                "    java.util.ArrayList accountList;\n" +
                "    java.lang.String currencyId;\n" +
                "    java.lang.String description;\n" +
                "    java.lang.String id;\n" +
                "    java.util.ArrayList items;\n" +
                "    java.lang.String name;\n" +
                "}";
        String className = "com.burtyka.cash.core.Account2";

        Class fooClass = generator.loadClass(sourceCode);
        assertNotNull(fooClass);
        assertEquals(fooClass.getCanonicalName(), className);
        assertNotNull(fooClass.getDeclaredMethods());
        assertTrue(fooClass.getDeclaredMethods().length > 0);
    }

    @Test
    public void generateClassFullProcess() {

        String file = "transactionManager.dat";
        Class generateClass = generator.generateClass(testDirPath + file);
        assertNotNull(generateClass);
//        assertEquals(generateClass.getCanonicalName(), className);
        assertNotNull(generateClass.getDeclaredMethods());
        assertTrue(generateClass.getDeclaredMethods().length > 0);
    }
}