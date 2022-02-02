package com.aihb.easyexcel;

import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 自定义拦截器，用于指定特定单元格样式
 */
public class SignRecordRowExcelHandler implements RowWriteHandler {

    private Map<Integer, ArrayList<Integer>> positionMap = new HashMap();

    public SignRecordRowExcelHandler(Map<Integer, ArrayList<Integer>> positionMap) {
        this.positionMap = positionMap;
    }

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        Sheet sheet = context.getWriteSheetHolder().getSheet();

        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//设置前景填充样式
        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());//前景填充色

        Integer row = context.getRowIndex();
        Set<Integer> rows = positionMap.keySet();
        if (!rows.contains(row)) return;

        ArrayList<Integer> cols = positionMap.get(row);
        for (Integer col : cols) {
            System.out.println(row + ":" + col);
            sheet.getRow(row).getCell(col).setCellStyle(cellStyle);
        }

        // System.out.println(sheet.getRow(row).getCell(2).getStringCellValue());  // 实体类加ExcelIgnore注解，不读取该列

    }


}
