package com.bzvir.reader;

import com.bzvir.model.Event;
import com.bzvir.util.AbstractTest;
import com.bzvir.util.FileUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

/**
 * Created by bohdan.
 */
public class Privat24XlsReaderTest extends AbstractTest {

    private Privat24XlsReader reader;

    @Before
    public void setUp() {
        String filePath = SAMPLE_DIR + "statements.xls";
        reader = new Privat24XlsReader(filePath, new FileUtil());
    }

    @Test
    public void loadP24XlsSheetModel() {
        Sheet sheet = reader.loadFirstSheet();

        assertThat(sheet, Matchers.isA(Sheet.class));
        Iterator<Row> rowIterator = sheet.rowIterator();

        assertThat(true, is(rowIterator.hasNext()));
        assertThat("There is no title row", sheet.getRow(1), notNullValue());
        assertThat("There is no at least one data row", sheet.getRow(2), notNullValue());
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
        Sheet sheet = reader.loadFirstSheet();
        Row row = sheet.getRow(2);
        Event event = reader.constructEvent(row);

        assertThat(event, Matchers.isA(Event.class));
        assertThat(event.getProperty("Сума у валюті картки"), instanceOf(Double.class));
        assertThat(event.getProperty("Дата"), instanceOf(String.class));
        assertThat(event.getProperty("Опис операції"), instanceOf(String.class));
        assertThat(event.getProperty("Категорія"), instanceOf(String.class));
        assertThat(event.getProperty("Валюта картки"), instanceOf(String.class));
    }

    @Test
    public void xlsFileUpdate() throws IOException {
        String filePath = SAMPLE_DIR + "statements.xls";
        FileUtil fileUtilMock = mock(FileUtil.class);
        doNothing().when(fileUtilMock).updateWorkbook(any(Workbook.class), anyString());

        Privat24XlsReader readerMock = new Privat24XlsReader(filePath, fileUtilMock);

        readerMock.saveToFileSystem();

        verify(fileUtilMock).updateWorkbook(any(Workbook.class), anyString());
    }

    @Test
    public void convertFromEventToRow() {
        Privat24XlsReader spy = spy(reader);
        doNothing().when(spy).updateWorkbook();

        String data1 = "data 1";
        String time1 = "time 1";
        String data2 = "data 2";
        String time2 = "time 2";
        Event event1 = dummyEvent("cat1", data1, time1, "desc");
        Event event2 = dummyEvent("cat2", data2, time2, "desc2");
        spy.convertFromEvent(toList(event1, event2));

        ArgumentCaptor<Row> rows = ArgumentCaptor.forClass(Row.class);
        verify(spy, times(2)).mapToRow(any(), rows.capture());

        Row actual1 = rows.getAllValues().get(0);
        assertEquals(data1, actual1.getCell(0).getStringCellValue());
        assertEquals(time1, actual1.getCell(1).getStringCellValue());
        Row actual2 = rows.getAllValues().get(1);
        assertEquals(data2, actual2.getCell(0).getStringCellValue());
        assertEquals(time2, actual2.getCell(1).getStringCellValue());
    }
}