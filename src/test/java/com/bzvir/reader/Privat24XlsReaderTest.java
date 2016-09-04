package com.bzvir.reader;

import com.bzvir.model.Event;
import com.bzvir.util.AbstractTest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by bohdan.
 */
public class Privat24XlsReaderTest extends AbstractTest {

    private Privat24XlsReader reader;
    private Sheet sheet;

    @Before
    public void setUp() {
        String filePath = SAMPLE_DIR + "statements.xls";
        reader = new Privat24XlsReader(filePath);
        sheet = reader.loadFirstSheet(filePath);
    }

    @Test
    public void loadXlsSheetModel() {
        assertThat(sheet, isA(Sheet.class));
        Iterator<Row> rowIterator = sheet.rowIterator();
        assertThat(true, is(rowIterator.hasNext()));
    }

    @Test
    public void checkOnRowTitles() {
        boolean theSame = reader.checkTitlesOnPresence();
        assertThat(theSame, is(true));
    }

    @Test
    public void loadData() throws Exception {
        List<Event> data = reader.loadData();
        assertThat(data, not(empty()));
        assertThat(data, everyItem(instanceOf(Event.class)));
    }

    @Test
    public void constructEventModelFromP24SheetRow() {
        Row row = sheet.getRow(2);
        Event event = reader.constructEvent(row);

        assertThat(event, isA(Event.class));
        assertThat(event.getProperty("Сума у валюті картки"), instanceOf(Double.class));
        assertThat(event.getProperty("Дата"), instanceOf(String.class));
        assertThat(event.getProperty("Опис операції"), instanceOf(String.class));
        assertThat(event.getProperty("Категорія"), instanceOf(String.class));
        assertThat(event.getProperty("Валюта картки"), instanceOf(String.class));
    }


}