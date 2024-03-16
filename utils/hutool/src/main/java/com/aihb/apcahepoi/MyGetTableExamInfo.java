package com.aihb.apcahepoi;

import cn.hutool.core.io.FileUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class MyGetTableExamInfo {

    @Test
    public void MyTest() {
        File directory = new File("D:\\Program Files\\JetBrains\\ideaProjects\\Heart\\utils\\hutool\\src\\main\\resources\\files\\套表"); // 替换为你的目录路径
        List<File> files = FileUtil.loopFiles(directory);
        for (File file : files) {
            System.out.println(file.getName());
            if (!file.getName().startsWith("~")) {
                MyProcess(file);
            }
        }
    }

    private static void MyProcess(File file) {
        Map<String, String> hashMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {
            ArrayList<List<Object>> results_list = new ArrayList<>();

            for (int sheetNo = 0; sheetNo < workbook.getNumberOfSheets(); sheetNo++) {

                Sheet sheet = workbook.getSheetAt(sheetNo);
                String sheetName = workbook.getSheetName(sheetNo);
                String tableName = hashMap.getOrDefault(sheetName, "");
                FormulaEvaluator formulaEvaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
                // 获取总行数和总列数
                int totalRows = sheet.getLastRowNum() + 1;

                if (totalRows == 2) {
                    Row row1 = sheet.getRow(0);
                    Row row2 = sheet.getRow(1);
                    int max = Math.max(row1.getLastCellNum(), row2.getLastCellNum());  // 两行中最大列号
                    for (int colIndex = 0; colIndex < max; colIndex++) {
                        Cell cell1 = row1.getCell(colIndex);
                        String col_name_zh = MyGetMergedRegionInfo.getCellValue(formulaEvaluator, cell1);
                        Cell cell2 = row2.getCell(colIndex);
                        String col_name = MyGetMergedRegionInfo.getCellValue(formulaEvaluator, cell2);
                        results_list.add(Arrays.asList(sheetName, tableName, col_name_zh.trim(), col_name, colIndex+1, 1));
                    }
                }

            }

            results_list.forEach(System.out::println);
            //results_list.forEach(row -> {
            //    try {
            //        Db.use().execute("insert into ai_test(col1, col2, col3, col4, col5) values (?, ?, ?, ?, ?)", row.get(0), row.get(1), row.get(2), row.get(3), row.get(4)) ;
            //    } catch (SQLException e) {
            //        throw new RuntimeException(e);
            //    }
            //});
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
