package com.aihb.excel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// https://blog.csdn.net/sfSam/article/details/84466708?spm=1001.2101.3001.6650.3&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-3.pc_relevant_paycolumn_v2&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-3.pc_relevant_paycolumn_v2&utm_relevant_index=6

public class ComparedExcel {
    @Test
    public void Main() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("D:\\Program Files\\JetBrains\\ideaProjects\\Heart\\Utils\\apache\\files\\test.xlsx");



        fileInputStream.close();
    }

}
