package com.aihb.apcahepoi;

import cn.hutool.db.Db;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import java.awt.Color;
import java.util.List;

public class ApachePOIExamples {

    /**
     * 1. Apache POI 读取 Excel，获取excel总行数、总列数、合并单元格位置、单元格样式
     */
    @Test
    public void getSheetInfo() {
        try (FileInputStream fis = new FileInputStream("");
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // 获取总行数和总列数
            int totalRows = sheet.getLastRowNum() + 1;
            int totalCols = 0;
            for (int i = 0; i < totalRows; i++) {
                if (null != sheet.getRow(i)) {
                    int cols = sheet.getRow(i).getLastCellNum();
                    if (cols > totalCols) {
                        totalCols = cols;
                    }
                }
            }

            // 获取合并单元格位置
            for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
                System.out.println("合并单元格位置：" + mergedRegion);
            }

            // 获取单元格样式
            for (Row row : sheet) {
                for (Cell cell : row) {
                    CellStyle style = cell.getCellStyle();
                    System.out.println("单元格样式：" + style);
                }
            }

            // 输出总行数和总列数
            System.out.println("总行数：" + totalRows);
            System.out.println("总列数：" + totalCols);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
