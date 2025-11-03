package com.data.job.task;

import com.data.config.CustomProperties;
import com.data.entity.ImpDataJobEntity;
import com.data.utils.FTPUtil;
import com.data.utils.ProStrUtil;
import com.data.utils.TarGzUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Slf4j
@Data
@Component
public class ReadGZFilesTask implements Runnable {
    private ImpDataJobEntity impDataJobEntity;
    private List<String> files;  // 所有需要处理文件
    private String curDate;  // 所有需要处理文件
    private CountDownLatch countDownLatch;

    private String[] tableColsArr;  // 文件字段名
    private String[] needColsArr;  // 入库字段名
    private Map<String, String> tableColsTypeMap;  // 字段类型Map
    String curFtpPath;

    /*** 以下无需创建对象时赋值 ***/
    @Autowired
    FTPUtil ftpUtil;
    @Autowired
    CustomProperties customProperties;
    @Autowired
    JdbcTemplate jdbcTemplate;

    private List<String> allDirFiles = new ArrayList<>();  // dir文件
    private List<String> allTarGzFiles = new ArrayList<>();  // tar.gz文件
    private boolean isCompress = true;
    private Long lineNum = 0L;
    private Character separator = '\u0001';
    private boolean isInitSucc = true;
    private Integer batchSize = 1000;

    public void setImpDataJobEntity(ImpDataJobEntity impDataJobEntity) {
        this.impDataJobEntity = impDataJobEntity;
    }

    public void setTableColsArr(String[] tableColsArr) {
        this.tableColsArr = tableColsArr;
    }

    public void setNeedColsArr(String[] needColsArr) {
        this.needColsArr = needColsArr;
    }

    public void setTableColsTypeMap(String tableColsTypeMap) {
        // 对象转JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(tableColsTypeMap);
            this.tableColsTypeMap = objectMapper.readValue(jsonString, Map.class);
        } catch (JsonProcessingException e) {
            isInitSucc = false;
            log.info("表结构 json 解析异常");
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
    }

    public void setCurFtpPath(String curFtpPath) {
        this.curFtpPath = curFtpPath;
    }

    /**
     * 从Ftp下载所有需要的文件到本地
     */
    private void downLoadFtpFiles() {
        ftpUtil.init(customProperties.getFtpHost(),
                Integer.parseInt(customProperties.getFtpPort()),
                customProperties.getFtpUsername(),
                customProperties.getFtpPasswd());
        try {
            ftpUtil.connect();
            for (String file : files) {
                ftpUtil.downloadFile(curFtpPath + file, impDataJobEntity.getFilePath());
            }
        } catch (IOException e) {
            isInitSucc = false;
            // TODO:创建连接重试
            log.info("ftp下载文件时失败！！！");
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
        ftpUtil.disconnect();
    }

    /**
     * 整理文件名
     * 1. 去掉该批次重推之前的无效文件
     * 2. dir list按字典序排序；后续按序找tar.gz处理
     */
    private void initFiles() {
        // 分开处理dir文件和tar.gz文件；后续按照dir文件去找tar.gz文件
        for (String file : files) {
            if (file.startsWith("dir.")) {
                allDirFiles.add(file);
            } else {
                allTarGzFiles.add(file);
            }
        }

        // 去掉该批次重复的重推文件，只取最新的
        List<Pair<String, String>> dirFilePair = new ArrayList<>();
        for (String dirFile : allDirFiles) {
            dirFilePair.add(Pair.of(ProStrUtil.getFileNameNoPushBatch(dirFile), dirFile));
        }
        // 按第一个元素分组，取第二个元素的最大值（字典序比较）
        allDirFiles = dirFilePair.stream()
                .collect(Collectors.groupingBy(
                        Pair::getLeft, // 按第一元分组
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Pair::getRight)), // 取第二元最大值
                                opt -> opt.get().getRight() // 取出第二元字符串
                        )
                ))
                .values() // 取 Map<String, String> 的值部分
                .stream()
                .collect(Collectors.toList());
        // 按字典序排序
        allDirFiles.sort(String::compareTo);
    }

    @Override
    public void run() {
        initFiles();
        downLoadFtpFiles();

        // 装配好的任务：files 为需要处理的文件；
        // ddl文件类型：新建表、新增字段、修改字段
        log.info(Thread.currentThread().getName());
        log.info(impDataJobEntity.toString());
        log.info(files.toString());

        if (!isInitSucc) return;

        // 获取所有tar.gz文件名称并整理好顺序；此时是dir的时间序；只需整理tar.gz中文件序即可
        List<Map<String, String>> dirFileText = new ArrayList<>();
        // 遍历所有该表 dir 文件
        for (String dirFile : allDirFiles) {
            try {
                List<String> dirinfo = TarGzUtil.readLines(dirFile);
                Map<String, String> dataFileInfoMap = new HashMap<>(5);
                // 遍历dir内容行
                for (String s : dirinfo) {
                    Map<String, String> fileInfoMap = new HashMap<>(5);
                    // fileInfoMap.clear();
                    String[] split = s.split(" ");
                    fileInfoMap.put("dirFileName", dirFile);
                    fileInfoMap.put("tarGzFileName", dirFile);
                    fileInfoMap.put("fileName", split[0]);
                    fileInfoMap.put("byteSize", split[1]);
                    fileInfoMap.put("lineNum", split[2]);
                    if (dirFile.endsWith(".i") && split[0].endsWith(".i.dat")) {
                        // 如果是增量I文件，调整下顺序，先放入del文件，最后放数据文件
                        dataFileInfoMap = fileInfoMap;
                        continue;
                    }
                    dirFileText.add(fileInfoMap);
                }
                dirFileText.add(dataFileInfoMap);   // 最后放数据文件
            } catch (IOException e) {
                log.info(String.format("读取文%s件报错", dirFile));
                e.printStackTrace();
                // throw new RuntimeException(e);
            }
        }

        // 按序处理文件并插入数据或处理ddl
        for (Map<String, String> fileInfo : dirFileText) {
            if (fileInfo.get("fileName").endsWith(".i.dat")) {
                // TODO：处理I数据文件；按主键先删后插
                log.info("处理I数据文件");
                readIncFileFromTarGz(fileInfo.get("tarGzFileName"), fileInfo.get("fileName"));
            } else if (fileInfo.get("fileName").endsWith(".i.del.dat")) {
                // TODO：处理I del文件；直接按主键删除
                log.info("处理I del文件");

            } else if (fileInfo.get("fileName").endsWith(".f")) {
                // TODO：处理f数据文件；直接插入
                log.info("处理f数据文件");
                readFullFileFromTarGz(fileInfo.get("tarGzFileName"), fileInfo.get("fileName"));
            } else {
                log.info("处理o文件");
                // 此时为o文件
                // init.lcs_vir.cv_l12346.20250918095959.ini
                // chgtyp.lcs_vir.cv_l12346.20250918095959.ddl
                // chgtyp.lcs_vir.cv_l12346.20250918095959.ini
                // 其他：增加字段
            }
        }

        // dir.lcs_vir.cv_l12346.20250918095959.000.000.00.i
        // vim lcs_vir.cv_l12346.20250918230000.000.000.00.i.dat.tar.gz
        // lcs_vir.cv_l12346.20250918230000.000.000.00.i.dat 0 0
        // lcs_vir.cv_l12346.20250918230000.000.000.00.i.del.dat 0 0


        // dir.lcs_vir.cv_l12346.20250918095959.000.000.00.f
        // vim lcs_vir.cv_l12346.20250918103000.000.000.00.f.dat.tar.gz
        // lcs_vir.cv_l12346.20250918103000.000.000.00.f.dat 2417911 40365

        // dir.lcs_vir.cv_l12346.20250918095959.000.000.00.o
        // vim lcs_vir.cv_l12346.20250918095959.000.000.00.o.dat.tar.gz
        // init.lcs_vir.cv_l12346.20250918095959.ini 281 7
        // chgtyp.lcs_vir.cv_l12346.20250918095959.ddl 281 7
        // chgtyp.lcs_vir.cv_l12346.20250918095959.ini 281 7


        countDownLatch.countDown();
    }


    /**
     * 从 tar.gz 中读取指定文件；处理f文件；与i文件逻辑稍有不同，使用PreparedStatement是否能快一点
     *
     * @param tarGzFilePath
     * @param targetFileName
     */
    // TODO：作为事务，不知道是否生效，待测试；Propagation.REQUIRES_NEW 保证每批独立事务
    @Transactional
    public void readFullFileFromTarGz(String tarGzFilePath, String targetFileName) {
        String insertSQL = buildInsertSQL(this.needColsArr);

        DataSource ds = jdbcTemplate.getDataSource();
        if (ds == null) {
            // TODO：是否需要重试
            log.info("数据源为空");
            // throw new SQLException("DataSource is null");
        }

        try (FileInputStream fis = new FileInputStream(tarGzFilePath);
             GzipCompressorInputStream gzis = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tais = new TarArchiveInputStream(gzis);
             Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSQL)) {
            conn.setAutoCommit(false);
            TarArchiveEntry entry;
            while ((entry = tais.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().equals(targetFileName)) {
                    log.info("找到文件并读取: " + entry.getName());
                    // 处理找到的文件
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(tais, StandardCharsets.UTF_8))) {
                        String line;
                        lineNum = 0L;
                        while ((line = reader.readLine()) != null) {
                            processDataLine(line, ps);
                            if (lineNum % batchSize == 0) {
                                ps.executeBatch();
                            }
                        }
                        ps.executeBatch();
                    }
                    conn.commit();
                    // TODO：维护日志表
                    return; // 找到并处理完目标文件后退出
                }
            }
            System.out.println("File not found: " + targetFileName);
        } catch (IOException e) {
            log.info("读取压缩文件异常");
            e.printStackTrace();
        } catch (SQLException e) {
            log.info("sql异常");
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
    }

    /**
     * 从 tar.gz 中读取指定文件；处理f文件；与i文件逻辑稍有不同，使用PreparedStatement是否能快一点
     *
     * @param tarGzFilePath
     * @param targetFileName
     */
    // TODO：作为事务，不知道是否生效，待测试；Propagation.REQUIRES_NEW 保证每批独立事务
    @Transactional
    public void readIncFileFromTarGz(String tarGzFilePath, String targetFileName) {
        DataSource ds = jdbcTemplate.getDataSource();
        if (ds == null) {
            // TODO：是否需要重试
            log.info("数据源为空");
            // throw new SQLException("DataSource is null");
        }

        try (FileInputStream fis = new FileInputStream(tarGzFilePath);
             GzipCompressorInputStream gzis = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tais = new TarArchiveInputStream(gzis);
             Connection conn = ds.getConnection();
             Statement statement = conn.createStatement()) {
            conn.setAutoCommit(false);
            TarArchiveEntry entry;
            while ((entry = tais.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().equals(targetFileName)) {
                    log.info("找到文件并读取: " + entry.getName());
                    // 处理找到的文件
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(tais, StandardCharsets.UTF_8))) {
                        String line;
                        lineNum = 0L;
                        while ((line = reader.readLine()) != null) {
                            processIncDataLine(line, statement);
                            if (lineNum % batchSize == 0) {
                                statement.executeBatch();
                            }
                        }
                        statement.executeBatch();
                    }
                    conn.commit();
                    // TODO：维护日志表
                    return; // 找到并处理完目标文件后退出
                }
            }
            System.out.println("File not found: " + targetFileName);
        } catch (IOException e) {
            log.info("读取压缩文件异常");
            e.printStackTrace();
        } catch (SQLException e) {
            log.info("sql异常");
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
    }

    private String buildInsertSQL(String[] columnNames) {
        StringBuilder sql = new StringBuilder();
        // TODO：此处表名应该另外配置，不应与文件名完全一致
        sql.append("INSERT INTO ").append(impDataJobEntity.getTableName()).append(" (");

        for (int i = 0; i < columnNames.length; i++) {
            sql.append(columnNames[i]);
            if (i < columnNames.length - 1) {
                sql.append(",");
            }
        }

        sql.append(") VALUES (");
        for (int i = 0; i < columnNames.length; i++) {
            sql.append("?");
            if (i < columnNames.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        return sql.toString();
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
                // TODO：转类型
                String columnValue = dataLineMap.get(columnName);
                String columnType = tableColsTypeMap.get(columnName);
                // addBatchByType(pstmt, i + 1, columnType, columnValue);
                Object obj = convertByType(columnType, columnValue);
                pstmt.setObject(i + 1, obj);
            }
            pstmt.addBatch();
            this.lineNum++;
        } catch (SQLException e) {
            log.info("数据插入异常：%s");
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
    }


    /**
     * 根据列类型，将字符串转换成对应 Java 对象
     *
     * @param columnType  数据库列类型，可能包含长度/精度，如 NUMBER(10,2)
     * @param columnValue 原始字符串
     * @return 对应类型对象（String, Integer, Long, BigDecimal, Date, Timestamp, Boolean）
     * @throws IllegalArgumentException 转换失败时抛出
     */
    private Object convertByType(String columnType, String columnValue) throws SQLException {
        if (columnValue == null || columnValue.trim().isEmpty()) {
            return null;
        }

        // 去掉长度和精度信息
        String type = columnType.toUpperCase().trim();
        if (type.contains("(")) {
            type = type.substring(0, type.indexOf("("));
        }

        switch (type) {
            case "VARCHAR":
            case "VARCHAR2":
            case "CHAR":
            case "NVARCHAR2":
            case "TEXT":
                // pstmt.setString(index, columnValue);
                // break;
                return columnValue;

            case "INT":
            case "INTEGER":
            case "SMALLINT":
            case "TINYINT":
                // pstmt.setInt(index, Integer.parseInt(columnValue));
                // break;
                return Integer.parseInt(columnValue);

            case "BIGINT":
                // pstmt.setLong(index, Long.parseLong(columnValue));
                // break;
                return Long.parseLong(columnValue);

            case "DECIMAL":
            case "NUMERIC":
            case "NUMBER":
                // Oracle NUMBER 类型可能有精度和小数位
                // pstmt.setBigDecimal(index, new BigDecimal(columnValue));
                // break;
                return new BigDecimal(columnValue);

            case "FLOAT":
            case "REAL":
            case "DOUBLE":
            case "DOUBLE PRECISION":
                // pstmt.setDouble(index, Double.parseDouble(columnValue));
                // break;
                return Double.parseDouble(columnValue);

            case "DATE":
                // 支持 yyyy-MM-dd 格式
                try {
                    java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(columnValue);
                    // pstmt.setDate(index, new java.sql.Date(utilDate.getTime()));
                    return new java.sql.Date(utilDate.getTime());
                } catch (ParseException e) {
                    log.info("无法解析日期: " + columnValue);
                    throw new SQLException("无法解析日期: " + columnValue, e);
                }
                // break;

            case "TIMESTAMP":
            case "DATETIME":
            case "TIMESTAMP(6)":
                // 支持 yyyy-MM-dd HH:mm:ss[.SSS]
                try {
                    java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(columnValue);
                    // pstmt.setTimestamp(index, new java.sql.Timestamp(utilDate.getTime()));
                    return new java.sql.Timestamp(utilDate.getTime());
                } catch (ParseException e) {
                    throw new SQLException("无法解析时间戳: " + columnValue, e);
                }
                // break;

            case "BOOLEAN":
            case "BIT":
                // 处理 Oracle/SQL Server/MySQL 布尔
                // pstmt.setBoolean(index, Boolean.parseBoolean(columnValue));
                // break;
                return Boolean.parseBoolean(columnValue);

            case "BLOB":
            case "LONGRAW":
            case "RAW":
                // 假设 columnValue 是 Base64 字符串
                // pstmt.setBytes(index, java.util.Base64.getDecoder().decode(columnValue));
                // break;
                return java.util.Base64.getDecoder().decode(columnValue);

            default:
                // fallback, 尝试当作字符串
                // pstmt.setString(index, columnValue);
                return columnValue;
        }
    }


    private void processIncDataLine(String line, Statement statement) {
        // 处理单行数据 - 实现数据解析和插入逻辑
        // 按分隔符切分字段值
        Map<String, String> dataLineMap = new HashMap();
        String[] data = line.split(String.valueOf(separator));
        for (int i = 0; i < data.length; i++) {
            dataLineMap.put(this.tableColsArr[i], data[i]);
        }

        // TODO：进行数据校验

        List<String> tabPK = Arrays.stream(impDataJobEntity.getTablePK().split(",")).map(String::trim).collect(Collectors.toList());
        // 执行插入操作
        StringBuilder delSql = new StringBuilder();
        // TODO：此处表名应该另外配置，不应与文件名完全一致
        delSql.append("delete from ").append(impDataJobEntity.getTableName()).append(" where 1 = 1 ");
        StringBuilder incSql1 = new StringBuilder();
        // TODO：此处表名应该另外配置，不应与文件名完全一致
        incSql1.append("insert into ").append(impDataJobEntity.getTableName()).append(" ( ");
        StringBuilder incSql2 = new StringBuilder();
        incSql2.append(" value (");
        try {
            for (int i = 0; i < needColsArr.length; i++) {
                String columnName = needColsArr[i];
                // TODO：转类型
                String columnType = tableColsTypeMap.get(columnName);
                String columnValue = dataLineMap.get(columnName);
                Object obj = convertByType(columnType, columnValue);
                incSql1.append(columnName).append(",");
                incSql2.append(obj).append(",");

                if (tabPK.contains(columnName)) {
                    delSql.append(" and ").append(columnName).append(" = ").append(obj);
                }
            }
            log.info("删除语句：" + delSql.toString());
            log.info("插入语句" + incSql1.deleteCharAt(incSql1.length() - 1).append(")").append(incSql2.deleteCharAt(incSql2.length() - 1).append(")")).toString());
            statement.addBatch(delSql.toString());
            statement.addBatch(
                    incSql1.deleteCharAt(incSql1.length() - 1)
                            .append(")")
                            .append(incSql2.deleteCharAt(incSql2.length() - 1).append(")"))
                            .toString()
            );
            this.lineNum++;
        } catch (SQLException e) {
            log.info("数据插入异常：%s");
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
    }

}