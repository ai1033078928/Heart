package com.aihb.io;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.net.URI;

public class ReadFile {

    @Test
    public void readFile() {

        String filePath = "D:\\Program Files\\JetBrains\\project\\Utils\\a_common\\zt.txt";

        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            System.out.println(IOUtils.toString(fileInputStream, "UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO异常");
            System.out.println(e.getMessage());
        }
        // InputStream inputStream = IOUtils.toInputStream("hello world!!!", "UTF-8");
    }


    @Test
    public void readURL() {
        try {
            String str = IOUtils.toString(URI.create("https://www.baidu.com/"), "UTF-8");
            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
