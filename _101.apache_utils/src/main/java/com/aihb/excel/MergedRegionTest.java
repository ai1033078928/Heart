package com.aihb.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MergedRegionTest {
    public static void main(String[] args) {
        // MergedRegionTest.getNewTestFile(null, null);
        Workbook workbook = MergedRegionTest.getTestFileByName("");

        IOUtils.closeQuietly(workbook);
    }

    public static Workbook getTestFileByName(String fileName) {
        Workbook workbook;
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            workbook = new XSSFWorkbook(fileIn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return workbook;
    }

    public static int getNewTestFile(String fileName, String sheetName) {
        if (null == fileName || fileName.isEmpty() ) fileName = "workbook.xlsx";
        if (null == sheetName || sheetName.isEmpty() ) fileName = "Sheet1";
        // 创建一个新的工作簿
        try (Workbook workbook = new XSSFWorkbook()) {
            // 创建一个工作表
            Sheet sheet = workbook.createSheet(sheetName);

            // 创建一个样式
            CellStyle decimalStyle = workbook.createCellStyle();
            // 设置小数位数为2
            decimalStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.0000"));

            for (int i = 0; i < 9 ; i++) {
                // 创建一个行
                Row row = sheet.createRow(i);
                for (int j = 0; j < 9; j++) {
                    // 创建一个单元格并设置值
                    Cell cell = row.createCell(j);
                    cell.setCellValue(4234223.45672342389); // 实际存储的数据
                    // 应用样式到单元格
                    cell.setCellStyle(decimalStyle);
                }
            }

            // 将工作簿保存到文件
            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
