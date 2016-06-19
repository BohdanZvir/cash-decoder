package com.bzvir.test;

/**
 * Created by bohdan on 19.06.16.
 */
import org.junit.Before;
import org.junit.Test;
import org.unsynchronized.jdeserialize;

import java.util.ArrayList;
import java.util.List;

public class CreatingClassTest {

    private static String testDirPath = System.getProperty("user.dir") + "/sample data/";

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void printEmptyClassDeclaration(){

        List<String> args = new ArrayList<>();
        args.add(testDirPath + "account.dat");
        args.add("-nocontent");
        args.add("-noinstances");
        args.add("-filter");
        args.add("java.util.*");

        jdeserialize.main(args.toArray(new String[]{}));
    }
}
