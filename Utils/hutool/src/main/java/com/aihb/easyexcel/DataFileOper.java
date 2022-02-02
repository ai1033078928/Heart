package com.aihb.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 调用api读取文件的类
 */
public class DataFileOper {

    private final String file = "D:\\Program Files\\JetBrains\\project\\Heart\\Utils\\hutool\\src\\main\\resources\\files\\test.xlsx";
    private final String file2 = "D:\\Program Files\\JetBrains\\project\\Heart\\Utils\\hutool\\src\\main\\resources\\files\\test2.xlsx";

//    @Test
    public List<MyData> readMain( String sourceFile ) {
        MyDataReadListener myDataReadListener = new MyDataReadListener();
        // 封装工作薄对象
        ExcelReaderBuilder workBook = EasyExcel.read(sourceFile, MyData.class, myDataReadListener);
        // 封装工作表对象
        ExcelReaderSheetBuilder sheet = workBook.sheet();
        // 读取
        sheet.doRead();
        return myDataReadListener.getAllData();
    }

    public void writeMain(String tagFile, List<MyData> dataList, HashMap<Integer, ArrayList<Integer>> map) {

//        SignRecordExcelHandler signRecordExcelHandler = new SignRecordExcelHandler();
//        signRecordExcelHandler.setPositionMap(map);

        EasyExcel.write(tagFile, MyData.class)
//                .registerWriteHandler(new SignRecordExcelHandler(map))
                .registerWriteHandler(new SignRecordRowExcelHandler(map))
                .sheet()
                .doWrite(dataList);

        System.out.println("生成对比后文件" + tagFile);
    }

    @Test
    public void writeMainTest() {
        ExcelWriterBuilder write = EasyExcel.write(file2, MyData.class);

        ExcelWriterSheetBuilder sheet = write.sheet();

        List<MyData> myData = readMain(file);

        sheet.doWrite(myData);
    }

    @Test
    public void MyMain() {

        List<MyData> data1 = readMain(file);
        List<MyData> data2 = readMain(file2);

        // 找出不同的单元格
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
        if ( data1.size() == data2.size() ) {

            for (int i = 0; i < data1.size(); i++) {
                ArrayList<Integer> cols = new ArrayList<>();
                MyData row1 = data1.get(i);
                MyData row2 = data2.get(i);

                if ( !row1.getId().equals(row2.getId()) ) {
                    cols.add(0);
                }

                if ( !row1.getCode().equals(row2.getCode()) ) {
                    cols.add(1);
//                    System.out.println(row1.getCode() + "!=" + row2.getCode());
                }
                // 表头一行，下标从0开始
                map.put(i + 1, cols);
            }

        } else {
            System.out.println("两个文件行数不同，退出");
        }

        writeMain("E:\\比较.xlsx", data2, map);


    }


}
