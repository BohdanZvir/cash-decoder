package com.bzvir.reader;

import com.bzvir.model.Category;
import com.bzvir.model.Event;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by bohdan.
 */
public class Privat24XlsReader implements Reader {

    private Sheet sheet;

    public Privat24XlsReader(String filePath) {
        sheet = loadFirstSheet(filePath);
    }

    public Map<String, String> readFile(String filePath) {
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.hasNext();
        Row row = rowIterator.next();

        Iterator<Cell> cellIterator = row.cellIterator();

        Map<String, String> map = new HashMap<>();
        return map;
    }

    @Override
    public Map<Category, Double> collectByCategories(LocalDate timeStart, LocalDate timeEnd) {
        return null;
    }

    public Sheet loadFirstSheet(String filePath) {
        Workbook wb = null;
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            wb = new HSSFWorkbook(file);  //-xls
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (wb != null) {
            return wb.getSheetAt(0);
        }
        return null;
    }

    @Override
    public Set<String> getTitles() {
        return new LinkedHashSet<>(Arrays.asList(
                "Сума у валюті картки",
                "Дата",
                "Час",
                "Опис операції",
                "Категорія", /*"Картка",*/
                "Валюта картки"/*, "Сума у валюті транзакції", "Валюта транзакції",
                "Залишок на кінець періоду", "Валюта залишку"*/));
    }

    private List<String> readRowTitles() {
        List<String> list = new LinkedList<>();
        Row row = sheet.getRow(1);
        row.forEach(s -> list.add(s.getStringCellValue()));
        return list;
    }

    @Override
    public boolean checkTitlesOnPresence() {
        Set<String> expected = getTitles();
        List<String> actual = readRowTitles();
        return actual.containsAll(expected);
    }

    @Override
    public Set<Event> loadData()  {
        List<String> rawTitles = readRowTitles();
        if (!rawTitles.containsAll(getTitles())) {
            throw new RuntimeException("Privat24 dump has wrong format.");
        }
        Set<Event> events = new HashSet<>();
        for (int i = 2; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            events.add(constructEvent(row));
        }
        return events;
    }

    public Event constructEvent(Row row) {
        Event event = new Event();
        List<String> actual = readRowTitles();
        for (String title : getTitles()) {
            int index = actual.indexOf(title);
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
