package com.bzvir.util;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.IsEmptyString.isEmptyString;

/**
 * Created by bohdan.
 */
public class DecoderTest extends AbstractTest {

//    private Decoder decoder;
    private ConsoleOutputCapturer capturer;

    private void captureConsoleOutput() {
        capturer = new ConsoleOutputCapturer();
        capturer.start(true);
    }

    private String getCapturedConsoleOutput() {
        return capturer.stop();
    }
//
//    @Before
//    public void setup() {
//        decoder = new Decoder();
//    }

    @Test
    public void printHelp() throws ParseException {
        captureConsoleOutput();

        Decoder.main(new String[]{"-help"});

        String output = getCapturedConsoleOutput();
        assertThat(output, not(isEmptyString()));
        assertThat(output, containsString("help"));
        assertThat(output, containsString("cash-with-p24"));
    }
}