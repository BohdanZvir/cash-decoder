package com.bzvir.reader;

import com.bzvir.model.Event;
import com.bzvir.util.FileUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.*;

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
                "Категорія",
                "Дата",
                "Час",
                "Сума у валюті картки",
                "Опис операції",
                "Валюта картки"
//                "Картка",
//                "Сума у валюті транзакції",
//                "Валюта транзакції",
//                "Залишок на кінець періоду",
//                "Валюта залишку"
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
            events.add(constructEvent(row));
        }
        return events;
    }

    @Override
    public void convertFromEvent(List<Event> p24) {
        // convert to Row.class objects
        // append to existed and sort it by date
        // update xls sheet with brand new Row list
    }

    @Override
    public void saveToFileSystem() {
        Workbook workbook = getXlsWorkbook();
        fileUtil.updateWorkbook(workbook, this.filePath);
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
}
