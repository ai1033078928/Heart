package com.hrbu.io.csv;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.text.csv.*;
import cn.hutool.core.util.CharsetUtil;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CsvTest {

    @Test
    public void wrtieTest() {
        //指定路径和编码
        CsvWriter writer = CsvUtil.getWriter("files/test.csv", CharsetUtil.CHARSET_UTF_8);
        //按行写出
        writer.write(
                new String[] {"a1", "b1\"", "c\"1"},
                new String[] {"a2", "b2", "c2"},
                new String[] {"a3", "b3", "c3"}
        );

    }


    @Test
    public void readTest() {
        CsvReader reader = CsvUtil.getReader();
        //从文件中读取CSV数据
        CsvData data = reader.read(FileUtil.file("files/test.csv"));
        List<CsvRow> rows = data.getRows();
        //遍历行
        for (CsvRow csvRow : rows) {
            //getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）
            Console.log(csvRow.getRawList());
            csvRow.getRawList().forEach(s -> Console.log(s));
            Console.log("=============================");
        }

    }

   @Test
   public void readWithOpenCSV() {
       try (CSVReader reader = new CSVReaderBuilder(new FileReader("files/test.csv"))
               .withCSVParser(
                       new CSVParserBuilder()
                               .withSeparator(',')      // 字段分隔符
                               .withQuoteChar('"')      // 引号字符
                               .withEscapeChar('\\')    // 转义字符（可选）
                               .withStrictQuotes(false) // 允许非严格引号闭合
                               .withIgnoreQuotations(false) // 不忽略引号
                       .build()
               )
               .build()) {

           // 方式1：逐行读取
           String[] nextLine;
           while ((nextLine = reader.readNext()) != null) {
               Console.log("Line: {}", Arrays.toString(nextLine));
               for (String s : nextLine) {
                   Console.log(s);
               }
           }

           // 方式2：一次性读取（适合小文件）
           // List<String[]> allData = reader.readAll();
           // allData.forEach(arr -> Console.log("Row: {}", Arrays.toString(arr)));

       } catch (IOException e) {
           throw new RuntimeException("CSV读取失败", e);
       } catch (CsvValidationException e) {
           throw new RuntimeException(e);
       }
   }


}
