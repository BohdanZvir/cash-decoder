package com.bzvir.util;

import com.burtyka.cash.core.Transaction;
import com.bzvir.model.Event;
import com.bzvir.reader.Reader;
import com.bzvir.reader.ReaderFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.unsynchronized.jdeserialize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class Decoder {

    public static final String APPLICATION_NAME = "cash-with-p24";

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
        capturer.start(true);
        jdeserialize.main(args.toArray(new String[]{}));
        String textline = capturer.stop();
//        System.out.println(" ============ hello from here ");
//        System.out.println(textline);

    }

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("help", false, "display help");
        options.addOption("p24", false, "use Privat24 xls file as source");
        options.addOption("generate", true,
                "[source] [target] generate classes from source path and put it into target path");

        CommandLine cmd = new DefaultParser().parse(options, args);

        boolean cashAsSource;

        Decoder decoder = new Decoder();
        if(cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
        } else if (cmd.hasOption("generate")) {
            String[] values = cmd.getOptionValues("generate");
            if (values == null || values.length == 0) {
                throw new RuntimeException("There are no source and no target path");
            } else if (values.length == 1) {
                decoder.doWork_2(values[0], values[0]);
            } else {
                decoder.doWork_2(values[0], values[1]);
            }
        } else {
            cashAsSource = !cmd.hasOption("p24");
            decoder.doWork(cashAsSource);
        }
    }

    private void doWork_2(String source, String target) {
        if (isNullOrEmpty(source)) {
            source = System.getProperty("user.dir") + "/sample data/";
        }
        if (isNullOrEmpty(target)) {
            target = source;
        }
        ClassGenerator generator = new ClassGenerator();
        generator.constructClasses(source, new File(target));
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
