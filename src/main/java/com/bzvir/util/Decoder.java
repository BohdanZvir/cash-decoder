package com.bzvir.util;

import com.burtyka.cash.core.Transaction;
import com.bzvir.model.Event;
import com.bzvir.reader.CashReader;
import com.bzvir.reader.Privat24XlsReader;
import com.bzvir.reader.Reader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.unsynchronized.jdeserialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Decoder {

    private static String dirPath;
    private static ResourceBundle resourceBundle;

    private Reader p24Reader;
    private Reader cashReader;

    public Decoder() {
        resourceBundle = ResourceBundle.getBundle("application");
        String p24File = dirPath + resourceBundle.getString("p24.file");
        p24Reader = new Privat24XlsReader(p24File);
        cashReader = new CashReader(dirPath, new FileUtil());
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

    public static void main(String[] args) {
//        jDeserial(dirPath + "settings.dat");
        String dataFilePath = dirPath + "settings.dat";
        ClassGenerator generator = new ClassGenerator();
//        Class loadedClass = generator.generateClassDeclarations(dataFilePath);
    }

    public List<Event> readP24() {
        return p24Reader.loadData();
    }

    public List<Event> readCash() {
        return cashReader.loadData();
    }

    public void saveToCash(List<Event> p24) {
        cashReader.reverseConvert(p24);
        cashReader.saveToFileSystem();
    }

    public void updateCash() {
        List<Event> p24 = readP24();
        saveToCash(p24);
    }
}
