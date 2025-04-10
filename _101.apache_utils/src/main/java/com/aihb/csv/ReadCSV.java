package com.aihb.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;
import sun.misc.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ReadCSV {
    @Test
    public void ReadCSV() {

        File file = new File("D:\\Program Files\\JetBrains\\project\\Heart\\utils\\hutool\\files\\test.csv");
        try {
            FileReader fileReader = new FileReader(file);
            List<CSVRecord> records = CSVFormat.newFormat('\u0001')
                    .parse(fileReader)
                    .getRecords();

            for (CSVRecord record : records) {
                System.out.println(record);
                for (String s : record) {
                    System.out.println(s);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }


}
