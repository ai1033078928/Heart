package com.aihb.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.io.IOException;

public class ReadExcel {

    private final String filepath = "D:\\Program Files\\JetBrains\\ideaProjects\\Heart\\Utils\\hutool\\src\\main\\resources\\files\\test.xlsx";

    @Test
    public void Main() throws IOException, InvalidFormatException {
//        String s = ExcelUtils.readExcel(filepath, 0);
//        System.out.println(s);

        Workbook workbook = ExcelUtils.getWorkbook(filepath);
        Sheet sheet = workbook.getSheetAt(0);


        Workbook workbook2 = ExcelUtils.getWorkbook(filepath);
        Sheet wbsheet = workbook.getSheetAt(0);

        CellStyle cellStyle = null;

        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            short lastCellNum = sheet.getRow(i).getLastCellNum();
            Row row = sheet.getRow(i);
            for (int j = 0; j < lastCellNum; j++) {
                if (row.getCell(j) == wbsheet.getRow(i).getCell(j)) {
                    wbsheet.getRow(i).getCell(j).setCellStyle(cellStyle);
                }
            }
        }

    }


}

