package com.bzvir;

import com.bzvir.model.Event;
import com.bzvir.generator.ClassGenerator;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class Decoder {

    private static final String APPLICATION_NAME = "cash-with-p24";

    public static void main(String[] args) throws ParseException {
        Options options = loadOptions();
        CommandLine cmd = new DefaultParser().parse(options, args);

        Decoder decoder = new Decoder();

        if(cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APPLICATION_NAME, options);
            
        } else if (cmd.hasOption("generate")) {
            String[] values = fixGenerateClassesArgs(cmd.getOptionValues("generate"));
            decoder.generateClasses(values[0], values[1]);
            
        } else {
            decoder.combineCashP24(!cmd.hasOption("p24"));
        }
    }

    private static Options loadOptions() {
        Options options = new Options();
        options.addOption("help", false, "display help");
        options.addOption("p24", false, "use Privat24 xls file as source");
        options.addOption("generate", true,
                "[source] [target] generate classes from source path and put it into target path");
        return options;
    }

    private static String[] fixGenerateClassesArgs(String[] args) {
        if (args == null || args.length == 0) {
            throw new RuntimeException("There are no source and no target path");
        } else if (args.length == 1) {
            return new String[] {args[0], args[0]};
        } else {
            return new String[] {args[0], args[1]};
        }
    }

    private void generateClasses(String source, String target) {
        if (isNullOrEmpty(source)) {
            source = System.getProperty("user.dir") + "/sample data/";
        }
        if (isNullOrEmpty(target)) {
            target = source;
        }
        ClassGenerator generator = new ClassGenerator();
        generator.constructClasses(source, new File(target));
    }

    private void combineCashP24(boolean cashIsSource) {
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
