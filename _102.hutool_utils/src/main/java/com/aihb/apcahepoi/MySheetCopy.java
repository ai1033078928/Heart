package com.aihb.apcahepoi;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MySheetCopy {

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Test
    public void test() {
        try (FileInputStream fis = new FileInputStream("D:\\Program Files\\JetBrains\\project\\Heart\\utils\\hutool\\src\\main\\resources\\files\\input.xlsx");
             Workbook inputWorkbook = WorkbookFactory.create(fis);
             FileOutputStream fos = new FileOutputStream("D:\\Program Files\\JetBrains\\project\\Heart\\utils\\hutool\\src\\main\\resources\\files\\output.xlsx")) {

            Workbook outputWorkbook = WorkbookFactory.create(true); // 创建新的工作簿

            // 遍历每个工作表
            for (int i = 0; i < inputWorkbook.getNumberOfSheets(); i++) {
                Sheet inputSheet = inputWorkbook.getSheetAt(i);
                Sheet outputSheet = outputWorkbook.createSheet(inputSheet.getSheetName());

                // 处理合并单元格
                for (int j = 0; j < inputSheet.getNumMergedRegions(); j++) {
                    CellRangeAddress mergedRegion = inputSheet.getMergedRegion(j);
                    if (mergedRegion != null) {
                        outputSheet.addMergedRegion(mergedRegion);
                    }
                }

                // 遍历每一行
                for (int rowNum = 0; rowNum <= inputSheet.getLastRowNum(); rowNum++) {
                    Row inputRow = inputSheet.getRow(rowNum);
                    Row outputRow = outputSheet.createRow(rowNum);

                    if (inputRow != null) {
                        // 遍历每一列
                        for (int colNum = 0; colNum < inputRow.getLastCellNum(); colNum++) {
                            Cell inputCell = inputRow.getCell(colNum);
                            Cell outputCell = outputRow.createCell(colNum);

                            if (inputCell != null) {
                                // 设置单元格值
                                switch (inputCell.getCellTypeEnum()) {
                                    case STRING:
                                        outputCell.setCellValue(inputCell.getStringCellValue());
                                        break;
                                    case NUMERIC:
                                        outputCell.setCellValue(inputCell.getNumericCellValue());
                                        break;
                                    case BOOLEAN:
                                        outputCell.setCellValue(inputCell.getBooleanCellValue());
                                        break;
                                    case FORMULA:
                                        outputCell.setCellFormula(inputCell.getCellFormula());
                                        break;
                                    default:
                                        break;
                                }

                                // 创建新的单元格样式并设置
                                CellStyle inputCellStyle = inputCell.getCellStyle();
                                CellStyle outputCellStyle = outputWorkbook.createCellStyle();
                                outputCellStyle.cloneStyleFrom(inputCellStyle);
                                outputCell.setCellStyle(outputCellStyle);
                            }
                        }
                    }
                }
            }


            outputWorkbook.createSheet("空Sheet页2");
            // 写出到文件
            outputWorkbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
