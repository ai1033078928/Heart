package com.aihb.hutoolexcel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import cn.hutool.poi.excel.sax.handler.RowHandler;

import java.util.List;

public class HutoolExcelExamples {

    /**
     * Sax方式读取Excel行处理器
     */
    private static RowHandler createRowHandler() {
        return (sheetIndex, rowIndex, rowList) -> {
            // 每一行的数据，即rowList
            System.out.println(rowList);
        };
    }

    /**
     * 1. 测试读取和写入
     */
    @Test
    public void readAndWriteExcel() {
        // 读取Excel文件
        ExcelReader reader = ExcelUtil.getReader("files/test.xlsx");
        ExcelWriter writer = ExcelUtil.getWriter("files/test3.xlsx");
        // 使用SAX方式读取Excel文件，可以处理大型Excel文件，避免OOM
        //ExcelUtil.readBySax("test.xlsx", 0,  (sheetIndex, rowIndex, rowList) -> {
        //    // 每一行的数据，即rowList
        //    System.out.println(rowList);
        //});

        List<String> sheetNames = reader.getSheetNames();
        sheetNames.forEach( sheetName -> {
            // 读取数据
            System.out.println(StrUtil.format("-------------- {} --------------", sheetName));
            List<List<Object>> read = reader.read();
            for (List<Object> row : read) {
                System.out.println(row);
            }

            // 写入数据
            // 设置Sheet页名称
            writer.setSheet(sheetName);
            //跳过当前行，既第一行，非必须，在此演示用
            writer.passCurrentRow();
            // 合并单元格后的标题行，使用默认标题样式
            writer.merge(2, StrUtil.format("测试标题_{}", sheetName));
            // 一次性写出内容，使用默认样式，强制输出标题
            writer.write(read, true);
        });

        reader.close();
        writer.close();
    }


    /**
     * 2. 流式读取
     */
    @Test
    public void getExcelByStream() {
        // 读取Excel文件
        ExcelUtil.readBySax("files/test3.xlsx", -1, new RowHandler() {
            @Override
            public void handle(int sheetIndex, long rowIndex, List<Object> rowlist) {
                Console.log("[{}] [{}] {}", sheetIndex, rowIndex, rowlist);
            }
        });
    }

}
