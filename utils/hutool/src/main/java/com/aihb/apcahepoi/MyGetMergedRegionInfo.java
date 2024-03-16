package com.aihb.apcahepoi;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyGetMergedRegionInfo {

    /**
     * 2. Apache POI 读取 Excel，获取excel总行数、总列数、合并单元格位置、单元格样式
     */
    @Test
    public void getMyInfo() {
        try (FileInputStream fis = new FileInputStream("D:\\Program Files\\JetBrains\\ideaProjects\\Heart\\utils\\hutool\\src\\main\\resources\\files\\xxx.xlsx");
             Workbook workbook = WorkbookFactory.create(fis)) {
            ArrayList<List<Object>> results_list = new ArrayList<>();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

                Sheet sheet = workbook.getSheetAt(i);
                // 遍历合并单元格并获取其值
                for (CellRangeAddress mergedRegion : sheet.getMergedRegions()) {
                    int firstRow = mergedRegion.getFirstRow();
                    int lastRow = mergedRegion.getLastRow();
                    int firstCol = mergedRegion.getFirstColumn();
                    int lastCol = mergedRegion.getLastColumn();
                    FormulaEvaluator formulaEvaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
                    // 获取合并单元格所在行的所有值
                    if (firstRow == 0 && lastRow == firstRow + 1) {
                        // 获取合并单元格的值
                        Row row1 = sheet.getRow(firstRow);
                        Row row2 = sheet.getRow(lastRow);
                        int max = Math.max(row1.getLastCellNum(), row2.getLastCellNum());  // 两行中最大列号
                        Cell cell = row1.getCell(firstCol);
                        String mergedCellValue = getCellValue(formulaEvaluator, cell);
                        // System.out.println("合并单元格位置：" + firstRow + "-" + lastRow + ", " + firstCol + "-" + lastCol);
                        // System.out.println("合并单元格的值：" + mergedCellValue);
                        for (int colIndex = firstCol + 1; colIndex < max; colIndex++) {
                            Cell cell1 = row1.getCell(colIndex);
                            String col_name_zh = getCellValue(formulaEvaluator, cell1);
                            Cell cell2 = row2.getCell(colIndex);
                            String col_name = getCellValue(formulaEvaluator, cell2);
                            int is_show = 0;
                            /*
                            int is_show = 1;
                            // 获取单元格样式
                            CellStyle cellStyle = cell1.getCellStyle();
                            // 获取单元格背景颜色
                            //short bgColorIndex = cellStyle.getFillForegroundColor();
                            //Color colorFromIndex = getColorFromIndex(workbook, bgColorIndex);
                            org.apache.poi.ss.usermodel.Color color = cellStyle.getFillForegroundColorColor();
                            if (color instanceof XSSFColor) {
                                byte[] rgb = ((XSSFColor) color).getRGB();
                                // 处理RGB颜色值
                                // rgb[0] 红色分量, rgb[1] 绿色分量, rgb[2] 蓝色分量
                                if (rgb != null) {
                                    Color color1 = new Color((rgb[0] & 0xFF), (rgb[1] & 0xFF), (rgb[2] & 0xFF));
                                    // java.awt.Color[r=155,g=194,b=230]   蓝色
                                    // java.awt.Color[r=117,g=189,b=66]
                                    // java.awt.Color[r=246,g=252,b=20]    黄色
                                    // java.awt.Color[r=255,g=255,b=0]
                                    // java.awt.Color[r=0,g=176,b=80]      绿色
                                    System.out.println(col_name_zh.trim() + "    " + color1);
                                    if (color1.getRed() == 117 || color1.getRed() == 246 || color1.getRed() == 255) is_show = 0;
                                }
                            }
                            */
                            results_list.add(Arrays.asList(mergedCellValue, col_name_zh.trim(), col_name, colIndex, is_show));  // 表名 字段中文 字段 列号 是否显示
                        }
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

    /**
     * 获取单元格的值
     */
    protected static String getCellValue(FormulaEvaluator formulaEvaluator, Cell cell) {
        if (cell == null) {
            return null;
        }
        // sheet.getWorkbook()：获取当前单元格所在的工作簿对象。
        // .getCreationHelper()：通过工作簿对象获取一个 CreationHelper 对象，用于创建各种对象，如 CellValue、RichTextString 等。
        // .createFormulaEvaluator()：通过 CreationHelper 对象创建一个 FormulaEvaluator 对象，用于计算 Excel 公式。
        // .evaluate(cell)：对指定的单元格进行求值，即计算单元格中的公式，并返回一个 CellValue 对象，其中包含了单元格的值。
        // CellValue cellValue = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator().evaluate(cell);
        CellValue cellValue = formulaEvaluator.evaluate(cell);

        switch (cellValue.getCellType()) {
            case STRING:
                return cellValue.getStringValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }


    /**
     * 获取单元格的颜色
     */
    private static java.awt.Color getColorFromIndex(Workbook workbook, short colorIndex) {
        java.awt.Color color = null;
        if (workbook instanceof HSSFWorkbook) {
            System.out.println("HSSFWorkbook");
            HSSFPalette palette = ((HSSFWorkbook) workbook).getCustomPalette();
            HSSFColor hssfColor = palette.getColor(colorIndex);
            if (hssfColor != null) {
                short[] rgb = hssfColor.getTriplet();
                color = new java.awt.Color(rgb[0], rgb[1], rgb[2]);
            }
        } else if (workbook instanceof XSSFWorkbook) {
            System.out.println("XSSFWorkbook");
            XSSFColor xssfColor = new XSSFColor();
            xssfColor.setIndexed(colorIndex);
//            System.out.println(xssfColor.getIndex());
            byte[] rgb = xssfColor.getRGB();
            if (rgb != null) color = new Color((rgb[0] & 0xFF), (rgb[1] & 0xFF), (rgb[2] & 0xFF));
        }
        return color;
    }
}
