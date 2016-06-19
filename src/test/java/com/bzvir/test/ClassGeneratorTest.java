package com.bzvir.test;

/**
 * Created by bohdan on 19.06.16.
 */
import org.junit.Before;
import org.junit.Test;
import org.unsynchronized.jdeserialize;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ClassGeneratorTest {

    private static String testDirPath = System.getProperty("user.dir") + "/sample data/";

    @Test
    public void printDirtyClassDeclaration(){

        List<String> args = new ArrayList<>();
        args.add(testDirPath + "transactionManager.dat");
        args.add("-nocontent");
        args.add("-noinstances");
        args.add("-filter");
        args.add("java.util.*");

        jdeserialize.main(args.toArray(new String[]{}));
    }

    @Test
    public void printEmptyClassDeclaration() {

        List<String> args = new ArrayList<>();
        args.add(testDirPath + "transactionManager.dat");
        args.add("-nocontent");
        args.add("-noinstances");
        args.add("-filter");
        args.add("java.util.*");

        ConsoleOutputCapturer capturer = new ConsoleOutputCapturer();
        capturer.start(false);
        jdeserialize.main(args.toArray(new String[]{}));
        String textLine = capturer.stop();
        textLine = ClassGenerator.fixClassDeclaration(textLine);
        System.out.println(textLine);
        assertNotNull(textLine);
        assertFalse(textLine.isEmpty());
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

        String fixedDeclaration = ClassGenerator.fixClassDeclaration(str);
        assertNotNull(fixedDeclaration);
        assertFalse(fixedDeclaration.contains("read:"));
        assertFalse(fixedDeclaration.contains("////"));
        int classIndex = fixedDeclaration.indexOf("class");
        int spaceIndex = fixedDeclaration.indexOf(' ', classIndex + 6);
        String className = fixedDeclaration.substring(classIndex + 6, spaceIndex);
        assertFalse(className.contains("."));
        assertTrue(Character.isUpperCase(className.charAt(0)));
    }

}