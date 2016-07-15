package com.bzvir.test;

/**
 * Created by bohdan on 19.06.16.
 */

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ClassGeneratorTest {

    private static String testDirPath;
    private ClassGenerator generator;
    private String sampleFile;
    private static ResourceBundle RESOURCE_BUNDLE;

    @BeforeClass
    public static void setUpBeforeClass() {
        RESOURCE_BUNDLE = ResourceBundle.getBundle("application");
    }

    @Before
    public void setUp() throws Exception {
        generator = new ClassGenerator();
        String sampleDir = RESOURCE_BUNDLE.getString("sample.dir");
        testDirPath = System.getProperty("user.dir") + sampleDir;
        sampleFile = testDirPath + "transactionManager.dat";
    }

    @Test
    public void printEmptyClassDeclaration() {

        List<String> args = ClassGenerator.getJdeserializeArgs();
        args.add(sampleFile);

        String textLine = generator.readClassDeclarations(args);
        List<String> strings = generator.getClassDeclaration(textLine);
        System.out.println(":: after capturing\n" + strings);

        assertTrue(strings.get(0).contains("class"));
    }

    @Test
    public void extractPackageName() {
        String str = "read: com.burtyka.cash.core.TransactionManager _h0x7e0003 = r_0x7e0000;  \n";
        String actual = generator.extractPackageName(str);
        assertThat(actual, equalTo("com.burtyka.cash.core"));
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

        List<String> fixedDeclaration = generator.getClassDeclaration(str);
        String fixed = fixedDeclaration.get(0);
        assertThat(fixed, not(containsString("read:")));
        assertThat(fixed, not(containsString("////")));

        int classIndex = fixed.indexOf("class");
        int spaceIndex = fixed.indexOf(' ', classIndex + 6);
        String className = fixed.substring(classIndex + 6, spaceIndex);

        assertThat(className, not(containsString(".")));
        assertTrue(Character.isUpperCase(className.charAt(0)));
        System.out.println(fixedDeclaration);
    }

    @Test
    public void generateClassFileWithPackageDirStructure() {
        String path = (RESOURCE_BUNDLE.containsKey("constructed.classes.package")) ?
                RESOURCE_BUNDLE.getString("constructed.classes.package")
                : System.getProperty("user.dir") + "/src/main/java";
        File pathToSave = new File(path);

        List<String> canonicalName = generator.constructClasses(sampleFile, pathToSave);

        List<String> paths = generator.convertCanonicalClassNames(path, canonicalName);

        assertThat(paths.size(), equalTo(canonicalName.size()));
    }

}