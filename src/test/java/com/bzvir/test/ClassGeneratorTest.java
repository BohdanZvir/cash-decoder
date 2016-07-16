package com.bzvir.test;

/**
 * Created by bohdan on 19.06.16.
 */

import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.text.StringContainsInOrder.stringContainsInOrder;
import static org.junit.Assert.*;

public class ClassGeneratorTest {

    private static String testDirPath;
    private ClassGenerator generator;
    private String sampleFile;
    private static ResourceBundle RESOURCE_BUNDLE;
    private File pathToSave;
    private List<File> created = new ArrayList<>();

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
        String path = (RESOURCE_BUNDLE.containsKey("constructed.classes.package"))
                ? RESOURCE_BUNDLE.getString("constructed.classes.package")
                : System.getProperty("user.dir") + "/src/main/java";
        pathToSave = new File(path);
    }

    @After
    public void tearDown() {
        created.stream().forEach(File::deleteOnExit);
        created.clear();
    }

    @Test
    public void printEmptyClassDeclarations() {
        String textLine = generator.readClassDeclarations(sampleFile);
        System.out.println(":: after capturing\n" + textLine);
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

        List<String> fixedDeclaration = generator.clearDeclarations(str);
        String actual = fixedDeclaration.get(0);
        assertThat(actual, not(allOf(
                containsString("read:"),
                containsString("////"),
                containsString("BEGIN"),
                containsString("END")
                )));
        assertThat(fixedDeclaration, everyItem(not(containsString("////"))));
//        assertThat("////", not(isOneOf(fixedDeclaration)));
//        assertThat(actual, stringContainsInOrder(Arrays.asList("{", "}")));
        System.out.println(fixedDeclaration);
        assertThat(fixedDeclaration, hasEqualNumber('{', '}'));
    }

    @Test
    public void extractClassNameWithoutPackage() {

        String fixed = "class com.burtyka.cash.core.Account implements java.io.Serializable ";
        int classIndex = fixed.indexOf("class") + "class ".length();
        int spaceIndex = fixed.indexOf(' ', classIndex);
        String className = fixed.substring(classIndex, spaceIndex);

        assertThat(className, not(containsString(".")));
        assertTrue(Character.isUpperCase(className.charAt(0)));
    }

    @Test
    public void generateClassFileWithPackageDirStructure() {
        Set<File> constructed = generator.constructClasses(sampleFile, pathToSave);
        created.addAll(constructed);

        boolean actualExist = false;
        for (File file : constructed) {
            actualExist |= file.exists();
            System.out.println(":: " + file.getAbsolutePath());
        }

        assertThat(constructed, hasItem(isA(File.class)));
        assertTrue(actualExist);
    }

    @Test
    public void ridOfJdeserializeCommentClassDeclarations() {
        String str = "read: com.burtyka.cash.core.TransactionManager _h0x7e0003 = r_0x7e0000;  \n" +
                "//// BEGIN class declarations (excluding array classes) (exclusion filter java.util.*)\n" +
                "class com.burtyka.cash.core.Transaction implements java.io.Serializable {\n" +
                "    double amount;\n" +
                "    float exchangeRate;\n" +
                "    java.lang.String date;\n" +
                "    java.lang.String description;\n" +
                "    java.lang.String fromAccountId;\n" +
                "    java.lang.String id;\n" +
                "    java.lang.String toAccountId;\n" +
                "}\n" +
                "\n" +
                "class com.burtyka.cash.core.TransactionManager implements java.io.Serializable {\n" +
                "    java.util.Map transactionPositionsToAccounts;\n" +
                "    java.util.ArrayList transasctions;\n" +
                "}\n" +
                "\n" +
                "//// END class declarations";
        List<String> collect = generator.clearDeclarations(str);
        StringBuilder sb = new StringBuilder();
        for (String s : collect) {
            sb.append(s).append("\n");
        }
        String actual = sb.toString();
        assertThat(actual, not(allOf(
                containsString("////"),
                containsString(":"))));
        assertThat(actual, stringContainsInOrder(Arrays.asList("{", "}")));
        System.out.println(actual);
    }

    @Test
    public void extractPackageName() {
        String str = "read: com.burtyka.cash.core.TransactionManager _h0x7e0003 = r_0x7e0000;  \n";
        String actual = generator.extractPackageName(str);
        assertThat(actual, equalTo("com.burtyka.cash.core"));
    }

    @Test
    public void notEqualNumberOfTwoCharsInText(){
        List<String> strings = Arrays.asList(" {{{", "{}", "}");
        assertThat(strings, not(hasEqualNumber('{', '}')));
    }

    private org.hamcrest.Matcher<List<String>> hasEqualNumber(char ch1, char ch2) {
        return new org.hamcrest.BaseMatcher<List<String>>() {
            @Override
            public void describeTo(Description description) {
                description.appendText(ch1 +" and " + ch2 + " has not equal number in text");
            }

            @Override
            public boolean matches(Object item) {
                if (item != null && item instanceof List && !((List)item).isEmpty()) {
                    return matchChars((List<String>) item);
                }
                return false;
            }

            private boolean matchChars(List<String> item) {
                int numberChar1 = 0;
                int numberChar2 = 0;
                for (String s : item) {
                    if (s !=null && !s.isEmpty()) {
                        for (char c : s.toCharArray()) {
                            if (c == ch1) {
                                numberChar1++;
                            } else if (c == ch2) {
                                numberChar2++;
                            }
                        }
                    }
                }
                return numberChar1 != 0 && numberChar1 == numberChar2;
            }
        };
    }

}