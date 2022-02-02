package com.aihb.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.apache.poi.ss.usermodel.CellType.*;

public class ReadExcel {

    private final String filepath = "D:\\Program Files\\JetBrains\\ideaProjects\\Heart\\Utils\\hutool\\src\\main\\resources\\files\\test.xlsx";
    private final String filepath2 = "D:\\Program Files\\JetBrains\\ideaProjects\\Heart\\Utils\\hutool\\src\\main\\resources\\files\\test - 副本.xlsx";

    @Test
    public void Main() throws IOException, InvalidFormatException {
//        String s = ExcelUtils.readExcel(filepath, 0);
//        System.out.println(s);

        Workbook workbook = ExcelUtils.getWorkbook(filepath);
        Sheet sheet = workbook.getSheetAt(0);


        Workbook workbook2 = ExcelUtils.getWorkbook(filepath2);
        Sheet wbsheet = workbook2.getSheetAt(0);

//        CellStyle cellStyle ;
        Map hashMap = new HashMap<Integer, Integer>();

        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            short lastCellNum = sheet.getRow(i).getLastCellNum();
            Row row = sheet.getRow(i);
            for (int j = 0; j < lastCellNum; j++) {
                Cell cell = row.getCell(j);
                Cell cell2 = wbsheet.getRow(i).getCell(j);

                switch (cell.getCellType()) {
                    case STRING://字符串类型
                        if ( !cell.getStringCellValue().equals(cell2.getStringCellValue()) ) hashMap.put(i, j);
                        break;
                    case NUMERIC: //数值类型
                        if ( cell.getNumericCellValue() != cell2.getNumericCellValue() ) hashMap.put(i, j);
                        break;
                    case FORMULA: //公式
                        System.out.println("===未处理公式==");
                        break;
                    case BLANK:
                        System.out.println("===未处理BLANK==");
                        break;
                    case BOOLEAN:
                        System.out.println("===未处理BOOLEAN==");
                        break;
                    case ERROR:
                        System.out.println("===未处理ERROR==");
                        break;
                    default:
                        System.out.println("===未处理==");
                        break;
                }
            }
            /*String col1 = row.getCell(0).getStringCellValue();
            String col2 = row.getCell(1).getStringCellValue();
            Date col3 = row.getCell(2).getDateCellValue();
            double col4 = row.getCell(3).getNumericCellValue();

            Row row2 = wbsheet.getRow(i);
            String wbcol1 = row2.getCell(0).getStringCellValue();
            String wbcol2 = row2.getCell(1).getStringCellValue();
            Date wbcol3 = row2.getCell(2).getDateCellValue();
            double wbcol4 = row2.getCell(3).getNumericCellValue();

            if ( !col1.equals(wbcol1) ) hashMap.put(i, 0);
            if ( !col2.equals(wbcol2) ) hashMap.put(i, 1);
            if ( !col3.equals(wbcol3) ) hashMap.put(i, 2);
            if ( col4 != wbcol4 ) hashMap.put(i, 3);*/


        }

        hashMap.forEach((k, v) ->{
            System.out.println(k + " : " + v);
        });

    }


}

