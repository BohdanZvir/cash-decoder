package com.bzvir.test;

import com.burtyka.cash.core.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.unsynchronized.JDeserialize;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Decoder {

    private static String dirPath = "/home/bohdan/Test/2016.01.25/";

    private static Map<Class, String> files = new HashMap<Class, String>();
    static {
        files.put(Settings.class, "settings.dat");
        files.put(Account.class, "account.dat");
        files.put(CurrencyManager.class, "currencyManager.dat");
        files.put(TransactionManager.class, "transactionManager.dat");
    }

    public static void main(String[] args) {
//		jDeserial(filePath);

        Settings settings = getValue(Settings.class);
        Account account = getValue(Account.class);
        TransactionManager transactionManager = getValue(TransactionManager.class);
        CurrencyManager currencyManager = getValue(CurrencyManager.class);

//		FileOutputStream fileOut =
//				new FileOutputStream(filePath + "(new)");
//		ObjectOutputStream out = new ObjectOutputStream(fileOut);
//		out.writeObject(transactionManager);

        printJsonValue(transactionManager);
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


    private static <T> T getValue(Class<T> clazz) {
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

				JDeserialize jdeserialize =  new JDeserialize(filePath);
				
				List<String> args2 = new ArrayList<String>();
				args2.add(filePath);
//				args2.add("-debug");
//				args2.add("-showarrays");
//				args2.add("-blockdata");
//				args2.add("/home/bogdan/Java/Cash/blockdata.txt");
//				args2.add("-blockdatamanifest");
//				args2.add("/home/bogdan/Java/Cash/blockdatamanifest.txt");
				jdeserialize.main(args2.toArray(new String[]{}));
	}

}
