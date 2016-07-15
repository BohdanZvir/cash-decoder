package com.bzvir.test;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 * Created by bohdan on 15.07.16.
 */
public class ClassLoaderUtilTest {

    @Test
    public void loadClassFromStringText() {
        String sourceCode = "package com.burtyka.cash.core;\n" +
                "\n" +
                "@lombok.Getter\n" +
                "@lombok.Setter\n" +
                "@lombok.ToString\n" +
                "public class Account2 implements java.io.Serializable {\n" +
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

        Class fooClass = new ClassLoaderUtil().loadClass(sourceCode);

        assertEquals(fooClass.getCanonicalName(), className);
        assertThat(fooClass.getDeclaredMethods().length, not(equalTo(0)));
    }
}