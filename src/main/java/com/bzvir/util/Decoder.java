package com.bzvir.util;

import com.burtyka.cash.core.Transaction;
import com.bzvir.model.Event;
import com.bzvir.reader.Reader;
import com.bzvir.reader.ReaderFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.unsynchronized.jdeserialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Decoder {

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

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("help", false, "display help");
        options.addOption("p24", false, "use Privat24 xls file as source");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if(cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("cash-with-p24", options);
        } else if(cmd.hasOption("p24")) {
            boolean cashIsSource = false;
            new Decoder().doWork(cashIsSource);
        } else {
            System.out.println("There are no args!!");
        }





//        jDeserial(dirPath + "settings.dat");
//        String dataFilePath = dirPath + "settings.dat";
//        ClassGenerator generator = new ClassGenerator();
//        Class loadedClass = generator.generateClassDeclarations(dataFilePath);

    }

    public void doWork(boolean cashIsSource) {
        Reader source;
        Reader target;
        if (cashIsSource) {
            source = ReaderFactory.createCashReader();
            target = ReaderFactory.createP24Reader();
        } else {
            source = ReaderFactory.createP24Reader();
            target = ReaderFactory.createCashReader();
        }

        List<Event> events = source.loadData();
        target.convertFromEvent(events);
        target.saveToFileSystem();
    }
}
