package com.heart.job.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heart.utils.ApacheFileUtil;
import com.heart.utils.SpringUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class ReadGzipFilesTask implements Runnable {

    // 获取 Logger 实例
    private static final Logger logger = LoggerFactory.getLogger(ReadGzipFilesTask.class);

    private String tableName;  // 库表名
    private String tableCols;  // 文件字段名
    private String[] tableColsArr;  // 文件字段名
    private String needCols;  // 入库字段名
    private String[] needColsArr;  // 入库字段名
    private String tableColsType;  // 字段类型Json串
    private Map tableColsTypeMap;  // 字段类型Map
    private String tablePK;   // 主键字段
    private String tableNotNullCols;  // 非空字段
    private String filesPath;
    private List<String> allFiles;
    private List<String> allDirFiles = new ArrayList<>();
    private boolean isCompress = true;
    private Long lineNum = 1L;
    private Connection conn;
    private Character separator = '\u0001';
    // private Map<String, List<String>> filesMap;
    private boolean isInitSucc = true;
    private Integer batchDataNum = 5000;


    CountDownLatch countDownLatch;

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }


    public ReadGzipFilesTask(String tableName, String tableCols, String needCols, String filesPath) {
        this.tableName = tableName;
        this.tableCols = tableCols;
        this.tableColsArr = Arrays.stream(tableCols.split(",")).map(String::trim).toArray(String[]::new);
        this.needCols = needCols;
        this.needColsArr = Arrays.stream(needCols.split(",")).map(String::trim).toArray(String[]::new);
        this.filesPath = filesPath;

        // 对象转JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(this.tableColsType);
            this.tableColsTypeMap = objectMapper.readValue(jsonString, Map.class);
        } catch (JsonProcessingException e) {
            System.out.println("表结构 json 解析异常");
            throw new RuntimeException(e);
        }

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
        DataSource dataDataSource = (DataSource) SpringUtils.getBean("datasource");
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
        countDownLatch.countDown();
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
            // 先处理 del 数据，再处理增量数据
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
        String insertSQL = buildInsertSQL();
        BufferedReader reader = null;
        try (FileInputStream fis = new FileInputStream(tarGzFilePath);
             GzipCompressorInputStream gzis = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tais = new TarArchiveInputStream(gzis);
             PreparedStatement preparedStatement = this.conn.prepareStatement(insertSQL)
        ) {
            TarArchiveEntry entry;
            while ((entry = tais.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    System.out.println("Reading file: " + entry.getName());
                    // 处理单个文件
                    reader = new BufferedReader(new InputStreamReader(tais, StandardCharsets.UTF_8));
                    String line;
                    this.lineNum = 0L;
                    StringBuilder strLineData = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        strLineData.append(line);
                        Integer count = getSeparatorCharCount(strLineData);
                        if (count == this.tableColsTypeMap.size()) {
                            processDataLine(strLineData.toString(), preparedStatement);
                            strLineData.setLength(0);
                        } else if (count < this.tableColsTypeMap.size()) {
                            continue;
                        } else {
                            System.out.println("单行分隔符过多，错误！！！");
                        }
                        if (this.lineNum % 5000 == 0) {
                            preparedStatement.executeUpdate();   // 5000条一批次提交
                        }
                    }
                    preparedStatement.executeUpdate();  // 最后批次提交
                }
            }
        } catch (IOException | SQLException e) {
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

    private Integer getSeparatorCharCount(StringBuilder stringBuilder) {
        // 统计特定字符 \u0001 的数量
        int count = 0;
        for (int i = 0; i < stringBuilder.length(); i++) {
            if (stringBuilder.charAt(i) == this.separator) {
                count++;
            }
        }
        return count;
    }


    /**
     * 从 tar.gz 中读取指定文件
     * @param tarGzFilePath
     * @param targetFileName
     */
    public void readSpecificFileFromTarGz(String tarGzFilePath, String targetFileName) {
        String insertSQL = buildInsertSQL();
        String delSQL = buildDelSQL();
        BufferedReader reader = null;
        try (FileInputStream fis = new FileInputStream(tarGzFilePath);
             GzipCompressorInputStream gzis = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tais = new TarArchiveInputStream(gzis);
             PreparedStatement delStmt = this.conn.prepareStatement(insertSQL);
             PreparedStatement insStmt = this.conn.prepareStatement(delSQL);
        ) {
            TarArchiveEntry entry;
            while ((entry = tais.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().equals(targetFileName)) {
                    // 处理单个文件
                    reader = new BufferedReader(new InputStreamReader(tais, StandardCharsets.UTF_8));
                    boolean isDelFile = targetFileName.endsWith(".i.del.dat");
                    String line;
                    lineNum = 1L;
                    StringBuilder strLineData = new StringBuilder();
                    Map<String, String> dataLine = new HashMap<>(needColsArr.length + 1);
                    List<String> fileHeadCols = null;
                    while ((line = reader.readLine()) != null) {
                        if (lineNum == 1L) {
                            // 初始化数据文件中表头
                            fileHeadCols = Arrays.asList(line.split(String.valueOf(separator)));
                            if (fileHeadCols.isEmpty()) {
                                logger.error("数据文件中表头错误，跳过文件");
                                return;
                            }
                        } else {
                            strLineData.append(line);
                            // 组装数据为 Map
                            assert fileHeadCols != null;
                            Integer count = getDataCols(strLineData.toString(), fileHeadCols, dataLine);
                            if (count == fileHeadCols.size()) {
                                processDataLine(dataLine, tablePK.split(String.valueOf(separator)), delStmt);
                                if (!isDelFile) {
                                    processDataLine(dataLine, needColsArr, insStmt);
                                }
                                lineNum++;
                                strLineData.setLength(0);
                                dataLine.clear();
                            } else if (count < fileHeadCols.size()) {
                                continue;
                            } else {
                                logger.error("单行数据分隔符过多，错误！！！");
                            }
                            if (lineNum % this.batchDataNum == 0) {
                                delStmt.executeUpdate();   // 批次提交
                                insStmt.executeUpdate();
                            }
                        }
                    }
                    delStmt.executeUpdate();  // 最后批次提交
                    insStmt.executeUpdate();
                    return; // 找到并处理完目标文件后退出
                }
            }
        } catch (IOException | SQLException e) {
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
     * 组装数据文件map
     * @param str
     * @param fileHeadCols
     * @param data
     * @return
     */
    private Integer getDataCols(String str, List<String> fileHeadCols, Map<String, String> data) {
        String[] dataArr = str.split(String.valueOf(this.separator));

        if (dataArr.length == fileHeadCols.size()) {
            for (int i = 0; i < fileHeadCols.size(); i++) {
                data.put(fileHeadCols.get(i), dataArr[i]);
            }
        }

        return dataArr.length;
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


    private void processDataLine(String line, PreparedStatement pstmt) {
        // 处理单行数据 - 实现数据解析和插入逻辑
        // 按分隔符切分字段值
        Map<String, String> dataLineMap = new HashMap();
        String[] data = line.split(String.valueOf(separator));
        for (int i = 0; i < data.length; i++) {
            dataLineMap.put(this.tableColsArr[i], data[i]);
        }

        // TODO：进行数据校验
        // 执行插入操作
        try {
            for (int i = 0; i < needColsArr.length; i++) {
                String columnName = needColsArr[i];
                String columnValue = dataLineMap.get(columnName);
                pstmt.setString(i + 1, columnValue);
            }
            pstmt.addBatch();
            this.lineNum++;
        } catch (SQLException e) {
            logger.error("数据插入单行提交异常");
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
    }


    private boolean validateData(String[] fieldValues, String[] columnNames) {
        // 解析元数据
        String[] pkColumns = this.tablePK.split(",");
        String[] notNullColumns = this.tableNotNullCols.split(",");

        // 校验非空字段
        for (String notNullCol : notNullColumns) {
            int index = getColumnIndex(notNullCol, columnNames);
            if (index != -1 && (fieldValues[index] == null || fieldValues[index].isEmpty())) {
                logger.error("非空字段 {} 为空", notNullCol);
                return false;
            }
        }

        // 校验主键字段不为空
        for (String pkCol : pkColumns) {
            int index = getColumnIndex(pkCol, columnNames);
            if (index != -1 && (fieldValues[index] == null || fieldValues[index].isEmpty())) {
                logger.error("主键字段 {} 为空", pkCol);
                return false;
            }
        }

        // 校验字段长度（需要解析tableColsType）
        // 这里需要根据实际的tableColsType JSON结构进行解析
        // 例如：{"col1":"VARCHAR(50)","col2":"NUMBER(10)"}

        return true;
    }

    private int getColumnIndex(String columnName, String[] columnNames) {
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private String buildInsertSQL() {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(this.tableName).append(" (");

        for (int i = 0; i < this.needColsArr.length; i++) {
            sql.append(this.needColsArr[i]);
            if (i < this.needColsArr.length - 1) {
                sql.append(",");
            }
        }

        sql.append(") VALUES (");
        for (int i = 0; i < this.needColsArr.length; i++) {
            sql.append("?");
            if (i < this.needColsArr.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        return sql.toString();
    }

    private String buildDelSQL() {
        String[] pkColumns = this.tablePK.split(",");
        // 构建删除SQL
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(this.tableName).append(" WHERE ");

        for (int i = 0; i < pkColumns.length; i++) {
            sql.append(pkColumns[i]).append(" = ?");
            if (i < pkColumns.length - 1) {
                sql.append(" AND ");
            }
        }

        return sql.toString();
    }

    private void processDataLine(Map<String, String> data, String[] cols, PreparedStatement pstmt) {
        // 处理单行数据
        try {
            for (int i = 0; i < cols.length; i++) {
                String columnName = cols[i];
                String colValue = data.get(columnName);
                pstmt.setObject(i + 1, colValue);   // TODO：处理数据类型
            }
            pstmt.addBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void processDdlLines(List<String> lines) {
        // 处理ini文件内容
        System.out.println(lines);
    }

}
