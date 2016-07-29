package com.bzvir.test;

import com.bzvir.model.Event;
import com.bzvir.reader.Privat24XlsReader;
import com.bzvir.reader.Reader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by bohdan on 18.06.16.
 */
public class DecoderTest {
    private final String SAMPLE_DIR = System.getProperty("user.dir") + "/sample data/";
    private Reader reader;
    private Sheet sheet;

    @Before
    public void setUp() {
        String filePath = SAMPLE_DIR + "statements.xls";
        reader = new Privat24XlsReader(filePath);
        sheet = reader.loadFirstSheet(filePath);
    }

//    @Test
//    public void readPrivat24Report() {
//        Map<String , String> result = reader.readFile(filePath);
//        assertNotNull(result);
//        assertFalse(result.isEmpty());
//
//        LocalDate timeStart = LocalDate.parse("2016-07-01");
//        LocalDate timeEnd = LocalDate.parse("2016-07-16");
//        Map<Category, Double> actual = reader.collectByCategories(timeStart, timeEnd);
//        assertThat(actual.keySet(), not(equalTo(0)));
//    }

    @Test
    public void loadXlsSheetModel() {
        assertThat(sheet, isA(Sheet.class));
        Iterator<Row> rowIterator = sheet.rowIterator();
        assertThat(true, is(rowIterator.hasNext()));
    }

    @Test
    public void checkOnRowTitles() {
        boolean theSame = reader.checkOnRowTitles();
        assertThat(theSame, is(true));
    }

    @Test
    public void loadData() throws Exception {
        Set<String> titles = reader.getRowTitles();
        List<Event> data = reader.loadData(titles);
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