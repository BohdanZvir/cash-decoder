package com.bzvir.util;

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
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.*;

public class ClassGeneratorTest {

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
        sampleFile = System.getProperty("user.dir")
                + RESOURCE_BUNDLE.getString("sample.dir")
                + "transactionManager.dat";
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
    public void generateClassFileWithPackageDirStructure() {
        Set<File> constructed = generator.constructClasses(sampleFile, pathToSave);
        created.addAll(constructed);

        boolean actualExist = false;
        for (File file : constructed) {
            actualExist |= file.exists();
        }

        assertThat(constructed, hasItem(isA(File.class)));
        assertTrue(actualExist);
    }

    @Test
    public void printEmptyClassDeclarations() {
        String textLine = generator.readClassDeclarations(sampleFile);

        assertTrue(textLine.contains("class"));
        assertThat(Arrays.asList(textLine), hasEqualNumber('{', '}'));
    }

    @Test
    public void getRidOfJdeserializationStuff() {
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

        List<String> actualList = generator.clearFromJdeserialization(str);
        String actual = actualList.get(0);

        assertThat(actual, not(allOf(
                containsString("read:"),
                containsString("BEGIN"),
                containsString("END")
                )));
        assertThat(actualList, everyItem(not(containsString("////"))));
        assertThat(actualList, hasEqualNumber('{', '}'));
    }

    @Test
    public void buildClassDeclarationsWithFiles() {
        List<String> lines = Arrays.asList(
                "class com.burtyka.cash.core.Transaction implements java.io.Serializable {",
                "    double amount;",
                "    float exchangeRate;",
                "    java.lang.String date;",
                "    java.lang.String description;",
                "    java.lang.String fromAccountId;",
                "    java.lang.String id;",
                "    java.lang.String toAccountId;",
                "}");
        Map<File, List<String>> map = generator.buildClassDeclarationsWithFiles(lines, pathToSave);

        Set<File> actual = map.keySet();
        assertThat(actual, not(empty()));
        map.forEach((k,v) -> {
            assertThat(v, hasEqualNumber('{','}'));
            assertThat(k, isA(File.class));
        });
    }

    @Test
    public void extractPackageNameFromClassCanonicalname() {
        String str = "com.burtyka.cash.core.TransactionManager";
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

    @Test
    public void extractClassNameWithoutPackage() {
        String fixed = "class com.burtyka.cash.core.Account implements java.io.Serializable ";
        int classIndex = fixed.indexOf("class") + "class ".length();
        int spaceIndex = fixed.indexOf(' ', classIndex);
        String className = fixed.substring(classIndex, spaceIndex);
        if (className.indexOf(".") > -1) {
            className = className.substring(className.lastIndexOf(".") + 1);
        }

        assertThat(className, not(containsString(".")));
        assertTrue(Character.isUpperCase(className.charAt(0)));
    }

}