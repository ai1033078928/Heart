package com.aihb.easyexcel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义拦截器，用于指定特定单元格样式
 */
public class SignRecordExcelHandler implements CellWriteHandler {

    private Map<Integer, ArrayList<Integer>> positionMap = new HashMap();

    public Map getPositionMap() {
        return positionMap;
    }

    public void setPositionMap(Map positionMap) {
        this.positionMap = positionMap;
    }

    public SignRecordExcelHandler() {
    }

    public SignRecordExcelHandler(Map<Integer, ArrayList<Integer>> positionMap) {
        this.positionMap = positionMap;
    }



    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        if (null == positionMap) return;

        Cell cell = context.getCell();
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();

        Workbook workbook = context.getWriteSheetHolder().getSheet().getWorkbook();
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());

        // 单元格下标若存在于map集合中，加前景色
        if (positionMap.containsKey(rowIndex)) {
            ArrayList<Integer> integers = positionMap.get(cell.getRowIndex());
            for (Integer integer : integers) {
                if (columnIndex == integer) {

                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//设置前景填充样式
                    cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());//前景填充色
                    cellStyle.setFont(font);

                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("不同：" + cell.getStringCellValue());
                    System.out.println("不同的单元格为" + rowIndex + "行" + columnIndex + "列" + cell.getStringCellValue());
                    break;
                }
            }
        }

    }

    /*
    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        if (null == positionMap) return;

        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();

        // 单元格下标若存在于map集合中，加前景色
        if (positionMap.containsKey(rowIndex)) {
            ArrayList<Integer> integers = positionMap.get(cell.getRowIndex());
            for (Integer integer : integers) {
                if (columnIndex == integer) {
                    Workbook workbook = writeSheetHolder.getSheet().getWorkbook();
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());//前景填充色
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//设置前景填充样式
//                    cellStyle.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
                    Font font = workbook.createFont();
                    font.setColor(IndexedColors.RED.getIndex());
                    cellStyle.setFont(font);
                    cell.setCellStyle(cellStyle);
                    System.out.println("不同的单元格为" + rowIndex + "行" + columnIndex + "列" + cell.getStringCellValue());
                }
            }
        }


    }
     */


}
