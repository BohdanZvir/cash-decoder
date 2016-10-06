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
    private Privat24XlsReader readerWithMock;
    private FileUtil fileUtilMock;

    @Before
    public void setUp() {
        String filePath = SAMPLE_DIR + "statements.xls";
        reader = new Privat24XlsReader(filePath, new FileUtil());

        fileUtilMock = mock(FileUtil.class);
        readerWithMock = new Privat24XlsReader(filePath, fileUtilMock);
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
        doNothing().when(fileUtilMock).updateWorkbook(any(Workbook.class), anyString());

        readerWithMock.saveToFileSystem();

        verify(fileUtilMock).updateWorkbook(any(Workbook.class), anyString());
    }

    @Test
    public void convertFromEventToRow() {
        doNothing().when(fileUtilMock).updateWorkbook(any(Workbook.class), anyString());

        Privat24XlsReader spy = spy(readerWithMock);

        Event event = dummyEvent("category", "date", "", "desc");
        spy.convertFromEvent(toList(event));

        ArgumentCaptor<Row> rows = ArgumentCaptor.forClass(Row.class);
        verify(spy).mapToRow(any(), rows.capture());

        Row actual = rows.getAllValues().get(0);
        assertEquals("date", actual.getCell(0).getStringCellValue());
        assertEquals("category", actual.getCell(2).getStringCellValue());
    }

    @Test
    public void eventsSortedByDateBeforeUpdateWorkbook() {
        doNothing().when(fileUtilMock).updateWorkbook(any(Workbook.class), anyString());
        Privat24XlsReader spy = spy(readerWithMock);
        Event event_1 = dummyEvent("cat1", "01.07.2016", "", "event 1");
        Event event_2 = dummyEvent("cat2", "29.06.2016", "", "event 2");
        Event event_3 = dummyEvent("cat3", "30.06.2016", "", "event 3");

        spy.convertFromEvent(toList(event_1, event_2, event_3));

        ArgumentCaptor<Row> rows = ArgumentCaptor.forClass(Row.class);
        verify(spy, times(3)).mapToRow(any(), rows.capture());

        Row actual_1 = rows.getAllValues().get(0);
        assertEquals("29.06.2016", actual_1.getCell(0).getStringCellValue());
        Row actual_2 = rows.getAllValues().get(1);
        assertEquals("30.06.2016", actual_2.getCell(0).getStringCellValue());
        Row actual_3 = rows.getAllValues().get(2);
        assertEquals("01.07.2016", actual_3.getCell(0).getStringCellValue());
    }

    @Test
    public void eventsSortedByTimeBeforeUpdateWorkbook() {
        doNothing().when(fileUtilMock).updateWorkbook(any(Workbook.class), anyString());
        Privat24XlsReader spy = spy(readerWithMock);
        Event event_1 = dummyEvent("cat", "01.07.2016", "19:43", "event 1");
        Event event_2 = dummyEvent("cat", "01.07.2016", "19:41", "event 2");
        Event event_3 = dummyEvent("cat", "01.07.2016", "19:45", "event 3");

        spy.convertFromEvent(toList(event_1, event_2, event_3));

        ArgumentCaptor<Row> rows = ArgumentCaptor.forClass(Row.class);
        verify(spy, times(3)).mapToRow(any(), rows.capture());

        Row actual_1 = rows.getAllValues().get(0);
        assertEquals("19:41", actual_1.getCell(1).getStringCellValue());
        Row actual_2 = rows.getAllValues().get(1);
        assertEquals("19:43", actual_2.getCell(1).getStringCellValue());
        Row actual_3 = rows.getAllValues().get(2);
        assertEquals("19:45", actual_3.getCell(1).getStringCellValue());

    }
}