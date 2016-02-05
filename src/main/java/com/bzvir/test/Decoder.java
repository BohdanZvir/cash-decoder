package com.bzvir.test;

import com.burtyka.cash.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.unsynchronized.JDeserialize;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Decoder {

	public static void main(String[] args) throws Exception {
		String basePath = "/home/bogdan/Dropbox/Apps/Cash_backup/25.01.2016-2/";
//		String basePath = "/home/bohdan/Test/25.01.2016-2/";
        Map<String, Class> files = new HashMap<String, Class>();
        files.put("settings.dat", Settings.class);
        files.put("account.dat", Account.class);
        files.put("currencyManager.dat", CurrencyManager.class);
        files.put("transactionManager.dat", TransactionManager.class);

		String filePath = "/home/bohdan/Test/25.01.2016-2/transactionManager.dat";
//		jDeserial(filePath);


		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(filePath)));
//		sett = (Settings) 
//		account = (Account)
//		currencyManager =		(CurrencyManager) 
        TransactionManager transactionManager = (TransactionManager)
				ois.readObject();

//		transactionManager.setTransasctions(new ArrayList<Transaction>());

//		FileOutputStream fileOut =
//				new FileOutputStream(filePath + "(new)");
//		ObjectOutputStream out = new ObjectOutputStream(fileOut);
//		out.writeObject(transactionManager);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(transactionManager);
        System.out.println(jsonInString);
//		getJson(transactionManager.getTransasctions());
		
//		System.out.println(transactionManager);
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
