package org.example.utils;

import cn.hutool.core.util.StrUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReadZip {
    /**
     * 读取并解压Zip文件到指定目录
     * @param zipFilePath Zip文件路径
     * @param outputDir 解压目标目录
     * @throws IOException 如果读取或解压过程中发生错误
     */
    /*public void unzip(String zipFilePath, String outputDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(outputDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!newFile.mkdirs()) {
                        throw new IOException("Failed to create directory: " + newFile.getAbsolutePath());
                    }
                } else {
                    // 确保父目录存在
                    File parentDir = newFile.getParentFile();
                    if (!parentDir.exists() && !parentDir.mkdirs()) {
                        throw new IOException("Failed to create parent directory: " + parentDir.getAbsolutePath());
                    }
                    // 写入文件内容
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }*/

    /**
     * 获取压缩包内所有文件的名称
     * @param zipFilePath Zip文件路径
     * @return 包含所有文件名的列表
     * @throws IOException 如果读取过程中发生错误
     */
    public static List<String> getZipFileNames(String zipFilePath) throws IOException {
        List<String> fileNames = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                fileNames.add(zipEntry.getName());
                zis.closeEntry();
            }
        }
        return fileNames;
    }

    /**
     * 获取压缩包中指定文件的内容
     * @param zipFilePath Zip文件路径
     * @param fileName 目标文件名
     * @return 包含文件内容的字符串
     * @throws IOException 如果读取过程中发生错误或未找到目标文件
     */
    public String getFileFromZip (String zipFilePath, String fileName) throws IOException {
        System.out.println(StrUtil.format("线程：{ThreadID} {ThreadName}  文件：{fileName}",
            new HashMap<String, String>() {{
                put("ThreadID", String.valueOf(Thread.currentThread().getId()));
                put("ThreadName", Thread.currentThread().getName());
                put("fileName", fileName);
            }}
        ));
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().equals(fileName)) {
                    // 使用InputStreamReader读取字符流
                    StringBuffer content = new StringBuffer();
                    InputStreamReader isr = new InputStreamReader(zis, "GBK");
                    char[] buffer = new char[1024];
                    int len;
                    while ((len = isr.read(buffer)) > 0) {
                        content.append(buffer, 0, len);
                    }
                    // System.out.println(StrUtil.subPre(content, 9));
                    zis.closeEntry(); // 1. 在返回前关闭当前条目
                    return content.toString();
                }
                zis.closeEntry();
            }
        }
        throw new RuntimeException("File not found in ZIP: " + fileName); // 明确抛出文件不存在异常
    }

    /**
     * 从压缩包数据字符串中读取指定文件的内容，以字节数组形式返回
     * @param bytes 压缩包数据
     * @return 结果
     */
    public String readDataFromBytesData(byte[] bytes) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    byteOutput.write(buffer, 0, len);
                }
                return new String(byteOutput.toByteArray(), "GBK");
            }
        }
        return "";
    }

}
