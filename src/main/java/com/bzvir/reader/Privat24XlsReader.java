package com.bzvir.reader;

import com.bzvir.model.Event;
import com.bzvir.util.FileUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Created by bohdan.
 */
public class Privat24XlsReader implements Reader {

    private Sheet sheet;
    private Workbook wb;
    private String filePath;
    private FileUtil fileUtil;

    public Privat24XlsReader(String filePath, FileUtil fileUtil) {
        this.filePath = filePath;
        sheet = loadFirstSheet();
        this.fileUtil = fileUtil;
    }

    Sheet loadFirstSheet() {
        Workbook wb = getXlsWorkbook();
        return (wb != null) ? wb.getSheetAt(0) : null;
    }

    private Workbook getXlsWorkbook() {
        if (wb != null) {
            return wb;
        }
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            wb = new HSSFWorkbook(file);  //-xls
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wb;
    }

    private Set<String> getTitles() {
        return new LinkedHashSet<>(Arrays.asList(
                "Дата",
                "Час",
                "Категорія",
                "Картка",
                "Опис операції",
                "Сума у валюті картки",
                "Валюта картки",
                "Сума у валюті транзакції",
                "Валюта транзакції",
                "Залишок на кінець періоду",
                "Валюта залишку"
        ));
    }

    private List<String> readRowTitles() {
        List<String> list = new LinkedList<>();
        Row row = sheet.getRow(1);
        row.forEach(s -> list.add(s.getStringCellValue()));
        return list;
    }

    boolean checkTitlesOnPresence() {
        Set<String> expected = getTitles();
        List<String> actual = readRowTitles();
        return (actual != null) && actual.containsAll(expected);
    }

    public List<Event> loadData()  {
        List<String> rawTitles = readRowTitles();
        if (!rawTitles.containsAll(getTitles())) {
            throw new RuntimeException("Privat24 dump has wrong format.");
        }
        List<Event> events = new LinkedList<>();
        for (int i = 2; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                events.add(constructEvent(row));
            }
        }
        return events;
    }

    @Override
    public void convertFromEvent(List<Event> events) {
        Comparator<Event> increaseDateTime = (e1, e2) -> readDateTime(e1).compareTo(readDateTime(e2));

        events.stream().sorted(increaseDateTime).forEach(e ->
                {
                    Row newRow = getNewRow();
                    mapEventOnRow(e, newRow);
                });
    }

    private LocalDateTime readDateTime(Event event) {
        return readDateTime(
                (String) event.getProperty("Дата"),
                (String) event.getProperty("Час"));
    }

    private LocalDateTime readDateTime(String date, String time) {
        String text = Objects.requireNonNull(date);

        String datePattern = "dd.MM.yyyy|HH:mm";
        if (text.matches("^\\d{4}-\\d{2}-\\d{2}")) {
            datePattern = "yyyy-MM-dd|HH:mm";
        }

        text += (isNullOrEmpty(time)) ? "|00:00" : '|' + time;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
        return LocalDateTime.parse(text, formatter);
    }

    private Row getNewRow() {
        int lastRowToShift = sheet.getLastRowNum();
        sheet.shiftRows(lastRowToShift, lastRowToShift, 1);
        return sheet.createRow(lastRowToShift);
    }

    Row mapEventOnRow(Event event, Row row) {
        List<String> titles = new ArrayList<>(getTitles());
        for (int i = 0; i < titles.size(); i++) {
            String title = titles.get(i);
            Object cellValue = event.getProperty(title);
//            if (i == 2) { // resolve Category
//                cellValue = event.getCategory();
//            }
            String value = "";
            if (cellValue instanceof Number || cellValue instanceof String) {
                value = cellValue.toString();
            }
            createCell(row, i, value);
        }
        return row;
    }

    private void createCell(Row row, int column, String value) {
        row.createCell(column).setCellValue(value);
    }

    @Override
    public void saveToFileSystem() {
        updateWorkbook();
    }

    Event constructEvent(Row row) {
        Event event = new Event();
        List<String> p24Titles = readRowTitles();

        for (String title : getTitles()) {
            int index = p24Titles.indexOf(title);
            Cell cell = row.getCell(index);

            int cellType = cell.getCellType();
            if (cellType == Cell.CELL_TYPE_STRING) {
                event.setProperty(title, cell.getStringCellValue());
            } else if (cellType == Cell.CELL_TYPE_NUMERIC) {
                event.setProperty(title, cell.getNumericCellValue());
            }
        }
        return event;
    }

    private void updateWorkbook() {
        fileUtil.updateWorkbook(this.wb, this.filePath);
    }
}
