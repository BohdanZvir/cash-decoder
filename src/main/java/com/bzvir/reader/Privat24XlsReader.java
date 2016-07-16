package com.bzvir.reader;

import com.bzvir.model.Category;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by bohdan on 20.06.16.
 */
public class Privat24XlsReader implements Reader {

    private String filePath;

    public Privat24XlsReader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Map<String, String> readFile(String filePath) {
        Sheet sheet = loadFirstSheet(filePath);

        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.hasNext();
        Row row = rowIterator.next();

        Iterator<Cell> cellIterator = row.cellIterator();

        HashMap<String, String> map = new HashMap<>();
        return map;
    }

    @Override
    public Map<Category, Double> collectByCategories(LocalDate timeStart, LocalDate timeEnd) {
        return null;
    }

    @Override
    public Sheet loadFirstSheet(String filePath) {
        Workbook wb = null;
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            wb = new HSSFWorkbook(file);  //-xls

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb.getSheetAt(0);
    }

    @Override
    public Set<String> getRowTitles() {
        return new HashSet<>(Arrays.asList(
                "Дата","Час", "Категорія", "Картка", "Опис операції", "Сума у валюті картки",
                "Валюта картки", "Сума у валюті транзакції", "Валюта транзакції",
                "Залишок на кінець періоду", "Валюта залишку"));
    }

    @Override
    public Set<String> readRowTitles(Sheet sheet) {
        Set<String> set = new HashSet<>();
        Row row = sheet.getRow(1);

        Iterator<Cell> iterator = row.iterator();
        while(iterator.hasNext()) {
            set.add(iterator.next().getStringCellValue());
        }
        return set;
    }
}
