package com.bzvir.util;

import com.burtyka.cash.core.*;
import com.bzvir.model.Event;
import com.bzvir.reader.Privat24XlsReader;
import com.bzvir.reader.Reader;
import com.bzvir.report.ShortReporter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.unsynchronized.jdeserialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class Decoder {

    private static String dirPath;
    private static ResourceBundle resourceBundle;

    private static Map<Class, String> files = new HashMap<>();
    static {
        files.put(Settings.class, "settings.dat");
        files.put(Account.class, "account.dat");
        files.put(CurrencyManager.class, "currencyManager.dat");
        files.put(TransactionManager.class, "transactionManager.dat");
    }

    private Reader p24Reader;
//    private Reader cashReader;

    public Decoder() {
        resourceBundle = ResourceBundle.getBundle("application");
        dirPath = System.getProperty("user.dir")
                + resourceBundle.getString("sample.dir") + "/";
        String p24File = dirPath + resourceBundle.getString("p24.file");
        p24Reader = new Privat24XlsReader(p24File);
//        cashReader = new CashReader(dirPath);
    }

    private static void printJsonValue(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = null;
        try {
            jsonInString = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(jsonInString);
    }


    protected static <T> T getValue(Class<T> clazz) {
        String filename = files.get(clazz);
        String filePath = dirPath + filename;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(filePath)));
            return clazz.cast(ois.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void getJson(List<Transaction> transasctions) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        for (Transaction transasction : transasctions) {

//Object to JSON in file
//        mapper.writeValue(new File("/home/bohdan/Test/25.01.2016-2/" + new Date() + ".json"), transasction);

//Object to JSON in String
            String jsonInString = mapper.writeValueAsString(transasction);
            System.out.println(jsonInString);
        }
	}

	private static void jDeserial(String filePath) {

        List<String> args = new ArrayList<>();
        args.add(filePath);

        ConsoleOutputCapturer capturer = new ConsoleOutputCapturer();
        capturer.start(false);
        jdeserialize.main(args.toArray(new String[]{}));
        String textline = capturer.stop();
//        System.out.println(" ============ hello from here ");
//        System.out.println(textline);

    }

    public static void main1(String[] args) {
//        jDeserial(dirPath + "settings.dat");
        String dataFilePath = dirPath + "settings.dat";
        ClassGenerator generator = new ClassGenerator();
//        Class loadedClass = generator.generateClassDeclarations(dataFilePath);
    }

    public static void main(String[] args) {

        Settings settings = getValue(Settings.class);
        Account account = getValue(Account.class);
        TransactionManager transactionManager = getValue(TransactionManager.class);
        CurrencyManager currencyManager = getValue(CurrencyManager.class);

        String report = new ShortReporter(account, transactionManager).doReport();
        System.out.println(report);


        //		FileOutputStream fileOut =
        //				new FileOutputStream(filePath + "(new)");
        //		ObjectOutputStream out = new ObjectOutputStream(fileOut);
        //		out.writeObject(transactionManager);

//        printJsonValue(transactionManager);
//        printJsonValue(account);
    }

    public List<Event> readP24() {
        Set<String> titles = p24Reader.getRowTitles();
        return p24Reader.loadData(titles);
    }

//    public List<Event> readCash() {
//        Account account = getValue(Account.class);
//        TransactionManager transactionManager = getValue(TransactionManager.class);
//
//        return null;
//    }
}
