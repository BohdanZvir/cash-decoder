package com.bzvir;

import com.bzvir.reader.CashReader;
import com.bzvir.reader.Privat24XlsReader;
import com.bzvir.util.FileUtil;

import java.util.ResourceBundle;

/**
 * Created by bohdan.
 */
public class ReaderFactory {
    private static String dirPath;
    private static ResourceBundle resourceBundle;

    private static Reader p24Reader;
    private static Reader cashReader;
    private static FileUtil fileUtil;

    static {
        resourceBundle = ResourceBundle.getBundle("application");
        dirPath = System.getProperty("user.dir")
                + resourceBundle.getString("sample.dir") + "/";
        fileUtil = new FileUtil();
    }

    public static Reader createCashReader() {
        if (cashReader == null) {
            cashReader = new CashReader(dirPath, fileUtil);
        }
        return cashReader;
    }

    public static Reader createP24Reader() {
        if (p24Reader == null) {
            String p24File = dirPath + resourceBundle.getString("p24.file");
            p24Reader = new Privat24XlsReader(p24File, fileUtil);
        }
        return p24Reader;
    }
}
