package com.heart.job.task;

import com.alibaba.druid.pool.DruidDataSource;
import com.heart.datasource.DynamicDataSource;
import com.heart.utils.ApacheFileUtil;
import com.heart.utils.SpringUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ReadGzipFilesTask implements Runnable {

    // 获取 Logger 实例
    private static final Logger logger = LoggerFactory.getLogger(ReadGzipFilesTask.class);

    private String tableName;
    private String tableCols;
    private String tableColsType;
    private String filesPath;
    private List<String> allFiles;
    private List<String> allDirFiles = new ArrayList<>();
    private boolean isCompress = true;
    private Long lineNum;
    private Connection conn;
    private String separator = "\\u0001";
    // private Map<String, List<String>> filesMap;
    private boolean isInitSucc = true;


    CountDownLatch countDownLatch;

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }


    public ReadGzipFilesTask(String tableName, String tableCols, String filesPath) {
        this.tableName = tableName;
        this.tableCols = tableCols;
        this.filesPath = filesPath;
        this.initFiles();
        this.initDbConn();
    }

    private void initFiles() {
        if (!Files.exists(Paths.get(this.filesPath))) {
            logger.error(String.format("错误！路径不存在：%s", this.filesPath));
            // throw new FileNotFoundException("");
            isInitSucc = false;
            return;
        }

        this.allFiles = ApacheFileUtil.getFilePathsInDirectoryByTableName(this.filesPath, this.tableName);
        this.allFiles.sort((o1, o2) -> {
            String[] o1Part = o1.split("\\.");
            String o1Time = o1.startsWith("dir.") ? o1Part[4] : o1Part[3];
            String[] o2Part = o2.split("\\.");
            String o2Time = o2.startsWith("dir.") ? o2Part[4] : o2Part[3];
            return o1Time.compareTo(o2Time);
        });

        String preIncFile = "";
        for (String fileName : this.allFiles) {
            if (fileName.endsWith(".i")) {
                if (this.isReSendBat(preIncFile, fileName)) {
                    this.allDirFiles.remove(preIncFile);
                }
                // this.allDirFiles.add(fileName);
                preIncFile = fileName;
            }/* else if (fileName.endsWith(".f")) {
                this.allDirFiles.add(fileName);
            } else if (fileName.endsWith(".o")) {
                this.allDirFiles.add(fileName);
            }*/
            this.allDirFiles.add(fileName);
         }
    }

    private void initDbConn() {
        // 与Durid配置文件指定的Bean一致
        DynamicDataSource dataDataSource = (DynamicDataSource) SpringUtils.getBean("datasource");
        try {
            // 此处druidDataSource.getConnection()获取到的类型是：com.alibaba.druid.pool.DruidPooledConnection，被druid包装过的，需要使用unwrap()转换一下
            this.conn = dataDataSource.getConnection();
            // conn = conn.unwrap(OracleConnection.class);
            if (!this.checkConnect()) this.isInitSucc = false;
        } catch (SQLException e) {
            isInitSucc = false;
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
    }

    private boolean checkConnect() {
        boolean isSucc = true;
        try (PreparedStatement stat = this.conn.prepareStatement("select 1")) {
            if (stat.execute()) return true;
        } catch (SQLException e) {
            // throw new RuntimeException(e);
        }
        return isSucc;
    }

    private boolean isReSendBat(String file1, String file2) {
        if (file1.isEmpty()) return false;

        String[] split1 = file1.split("\\.");
        String[] split2 = file2.split("\\.");
        boolean resFlag = true;
        for (int i = 0; i < split1.length; i++) {
            if (i != 6) {
                if (!split1[i].equals(split2[i])) {
                    resFlag = false;
                }
            }
        }
        return resFlag;
    }


    @Override
    public void run() {
        System.out.println(this.conn);

        if (!isInitSucc) return;
        // 遍历所有该表 dir 文件
        for (String dirFile : this.allDirFiles) {
            List<Map<String, String>> dirFileText = new ArrayList<>();
            Map<String, String> lineMap = new HashMap<>();
            try {
                List<String> dirinfo = ApacheFileUtil.readLines(dirFile);
                dirinfo.forEach(s -> {
                    lineMap.clear();
                    String[] split = s.split(" ");
                    lineMap.put("dirFileName", dirFile);
                    lineMap.put("fileName", split[0]);
                    lineMap.put("byteSize", split[1]);
                    lineMap.put("lineNum", split[2]);
                    dirFileText.add(lineMap);
                });
            } catch (IOException e) {
                e.printStackTrace();
                // throw new RuntimeException(e);
            }

            if (dirFile.endsWith(".i")){
                parseIncFile(dirFileText);
            } else if (dirFile.endsWith(".f")) {
                parseFullFile(dirFileText);
            } else if (dirFile.endsWith(".o")) {
                parseDdlFile(dirFileText);
            }
        }
        try {
            this.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
    }

    private void parseIncFile(List<Map<String, String>> dirFile) {
        // lcs_vir.cv_l12346.20250918230000.000.000.00.i.dat 0 0
        // lcs_vir.cv_l12346.20250918230000.000.000.00.i.del.dat 0 0
        // vim lcs_vir.cv_l12346.20250918230000.000.000.00.i.dat.tar.gz
        if (null == dirFile || dirFile.isEmpty()) return;

        // TODO：指定文件顺序，根据顺序处理文件
        List<String> delFiles = new ArrayList<>();
        List<String> incFiles = new ArrayList<>();
        for (Map<String, String> line : dirFile) {
            if (line.get("fileName").endsWith(".i.del.dat")) {
                delFiles.add(line.get("fileName"));
            } else if(line.get("fileName").endsWith(".i.dat")) {
                incFiles.add(line.get("fileName"));
            }
        }

        String gzipFileName = dirFile.get(0).get("dirFileName").substring(4) + ".dat.tar.gz";
        if (isCompress) {
            delFiles.forEach(file -> this.readSpecificFileFromTarGz(gzipFileName, file));  // 执行delete数据
            incFiles.forEach(file -> this.readSpecificFileFromTarGz(gzipFileName, file));  // 执行增量数据
        }
    }

    private void parseFullFile(List<Map<String, String>> dirFile) {
        // lcs_vir.cv_l12346.20250918103000.000.000.00.f.dat 2417911 40365
        // vim lcs_vir.cv_l12346.20250918103000.000.000.00.f.dat.tar.gz
        if (null == dirFile || dirFile.isEmpty()) return;
        String gzipFileName = dirFile.get(0).get("dirFileName").substring(4) + ".dat.tar.gz";
        if (isCompress) {
            this.readAllFileTarGz(gzipFileName);
        }
    }

    private void parseDdlFile(List<Map<String, String>> dirFile) {
        // init.lcs_vir.cv_l12346.20250918095959.ini 281 7
        // vim lcs_vir.cv_l12346.20250918095959.000.000.00.o.dat.tar.gz
        if (null == dirFile || dirFile.isEmpty()) return;
        String gzipFileName = dirFile.get(0).get("dirFileName").substring(4) + ".dat.tar.gz";
        if (isCompress) {
            List<String> ddlLines = this.readSpecificDdlFileFromTarGz(gzipFileName, dirFile.get(0).get("fileName"));
            processDdlLines(ddlLines);
        }
    }

    /**
     * 从 tar.gz 中读取所有
     * @param tarGzFilePath
     */
    public void readAllFileTarGz(String tarGzFilePath) {
        BufferedReader reader = null;
        try (FileInputStream fis = new FileInputStream(tarGzFilePath);
             GzipCompressorInputStream gzis = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tais = new TarArchiveInputStream(gzis)) {

            TarArchiveEntry entry;
            while ((entry = tais.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    System.out.println("Reading file: " + entry.getName());
                    // 处理单个文件
                    reader = new BufferedReader(new InputStreamReader(tais, StandardCharsets.UTF_8));
                    String line;
                    this.lineNum = 0L;
                    while ((line = reader.readLine()) != null) {
                        processDataLine(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    // throw new RuntimeException(e);
                }
            }
        }
    }


    /**
     * 从 tar.gz 中读取指定文件
     * @param tarGzFilePath
     * @param targetFileName
     */
    public void readSpecificFileFromTarGz(String tarGzFilePath, String targetFileName) {
        try (FileInputStream fis = new FileInputStream(tarGzFilePath);
             GzipCompressorInputStream gzis = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tais = new TarArchiveInputStream(gzis)) {

            TarArchiveEntry entry;
            while ((entry = tais.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().equals(targetFileName)) {
                    System.out.println("Found and reading file: " + entry.getName());
                    // 处理找到的文件
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(tais, StandardCharsets.UTF_8))) {
                        String line;
                        this.lineNum = 0L;
                        while ((line = reader.readLine()) != null) {
                           processDataLine(line);
                        }
                    }
                    return; // 找到并处理完目标文件后退出
                }
            }
            System.out.println("File not found: " + targetFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<String> readSpecificDdlFileFromTarGz(String tarGzFilePath, String targetFileName) {
        List<String> resList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(tarGzFilePath);
             GzipCompressorInputStream gzis = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tais = new TarArchiveInputStream(gzis)) {

            TarArchiveEntry entry;
            while ((entry = tais.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().equals(targetFileName)) {
                    System.out.println("Found and reading file: " + entry.getName());
                    // 处理找到的文件
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(tais, StandardCharsets.UTF_8))) {
                        String line;
                        this.lineNum = 0L;
                        while ((line = reader.readLine()) != null) {
                            resList.add(line);
                        }
                    }
                    return resList; // 找到并处理完目标文件后退出
                }
            }
            System.out.println("File not found: " + targetFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resList;
    }


    private void processDataLine(String line) {
        // 处理单行数据
        System.out.println(line);
    }

    private void processDdlLines(List<String> lines) {
        // 处理ini文件内容
        System.out.println(lines);
    }

}
