package org.example;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.setting.dialect.Props;
import org.example.utils.ReadZip;
import org.example.utils.XmlUtil;
import org.junit.Test;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MyTest {

    private final static Props props = new Props("default.properties");


    @Test
    // 处理 ZIP 文件的任务队列
    public void processZipFiles() {
        ExecutorService executor = Executors.newFixedThreadPool(9); // 创建固定大小的线程池

        List dbConf;
        try {
            dbConf = Db.use("db1").query((String) props.get("config_table"), props.get("call_type"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        for (Object o : dbConf) {
            Entity entity = (Entity) o;
            hashMap.put(entity.getStr((String) props.get("data_type_field")), entity);
        }

        try {
            // 获取 ZIP 文件名列表
            List<String> zipFileNames = ReadZip.getZipFileNames((String) props.get("zipPath"));

            // 文件名解析与分组逻辑
            Map<String, List<String>> groupedFiles = zipFileNames.stream()
                    .filter(x -> !x.equals("Documentation.xml"))
                    .map(name -> {
                        // 正确拆分文件名（包含.xml扩展名会被拆分为7个部分）
                        String[] parts = name.replace(".xml", "").split("-");
                        if (parts.length < 6) {
                            throw new IllegalArgumentException("Invalid file name format: " + name);
                        }
                        return new FileNameInfo(parts, name); // 传递完整parts数组
                    })
                    // 调整排序逻辑为按firstPart和fifthPart排序
                    .sorted(Comparator.comparing(FileNameInfo::getFirstPart)
                            .thenComparing(FileNameInfo::getFifthPart))
                    .collect(Collectors.groupingBy(
                            FileNameInfo::getFourthPart, // 假设需要按ELE_JGDW分组（需确认字段）
                            LinkedHashMap::new,
                            Collectors.mapping(FileNameInfo::getOriginalName, Collectors.toList())
                    ));

            // 提交任务到线程池
            for (Map.Entry<String, List<String>> entry : groupedFiles.entrySet()) {
                executor.submit(() -> processGroup(entry.getKey(), entry.getValue(), hashMap));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading ZIP file names", e);
        } finally {
            executor.shutdown(); // 关闭线程池
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) { // 等待任务完成
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for executor termination", e);
            }
        }
    }

    /*@Test
    public void test() {
        processGroup("", Arrays.asList("1-all-3200-rzpt_mlxx-1-2.xml"), null);
    }*/


    // 处理每组文件的任务
    private void processGroup(String groupKey, List<String> fileNames, Map dbConf) {
        // System.out.println(StrUtil.format("======= processGroup =========\n{}", fileNames.toString()));
        ReadZip readZip = new ReadZip();
        XmlUtil xmlUtil = new XmlUtil();

        for (String fileName : fileNames) {
            try {
                // 读取文件内容
                String data = readZip.getFileFromZip((String) props.get("zipPath"), fileName);
                // System.out.println(data);

                byte[] bytes = new BASE64Decoder().decodeBuffer(data);
                String string = readZip.readDataFromBytesData(bytes);

                Pattern p = Pattern.compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFF]+");
                String xml = p.matcher(string).replaceAll("");

                List<Map> rows = xmlUtil.parseXmlString(xml);
                System.out.println(StrUtil.format("行数：{}", rows.size()));

                // 动态生成表名
                Entity entity = (Entity) dbConf.get(groupKey);
                String tableName = entity.getStr((String) props.get("target_table_field"));  // fileName.split("\\.")[0].replaceAll("-", "_");

                List<String> validTableNames = Arrays.asList(props.get("exclude_table").toString().split(","));
                if (!validTableNames.contains(tableName)) {
                    // 执行你的逻辑
                    // System.out.println("tableName: " + tableName);
                    // 插入数据到数据库
                    insertDataToTable(tableName, rows);
                }

            } catch (Exception e) {
                System.err.println("Error processing file: " + fileName);
                e.printStackTrace();
            }
        }
    }

    // 插入数据到指定表
    private void insertDataToTable(String tableName, List<Map> rows) throws SQLException {
        if (rows.isEmpty()) return;

        // 初始化 columns 和 placeholders
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        // 从第一条数据获取字段名
        Map<String, Object> firstRow = rows.get(0);
        for (String key : firstRow.keySet()) {
            columns.append(key).append(", ");
            placeholders.append("?, ");
        }

        // 去除最后一个逗号
        columns.setLength(columns.length() - 2);
        placeholders.setLength(placeholders.length() - 2);

        // 构建 SQL 模板
        String insertSql = StrUtil.format(
            "INSERT INTO {table}({columns}) VALUES({placeholders})",
            new HashMap<String, String>() {{
                put("table", tableName);
                put("columns", columns.toString());
                put("placeholders", placeholders.toString());
            }}
        );

        // 批量插入数据
        Db.use("db1").tx(conn -> {
            try (PreparedStatement ps = conn.getConnection().prepareStatement(insertSql)) {
                for (Map<String, Object> row : rows) {
                    int i = 1;
                    for (Object value : row.values()) {
                        ps.setObject(i++, value);
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException e) {
                throw new RuntimeException("Error inserting data into table: " + tableName, e);
            }
        });
    }

    // 定义文件名信息类
    class FileNameInfo {
        private final String firstPart;
        private final String fourthPart;
        private final String fifthPart;
        private final String originalName;

        // 将构造函数参数改为接收完整parts数组
        public FileNameInfo(String[] parts, String originalName) {
            this.firstPart = parts[0];          // 第一个1
            this.fourthPart = parts[3];        // 第四个部分（如"1"）
            this.fifthPart = parts[4];         // 第五个部分（如"1"）
            this.originalName = originalName;
        }

        public String getFirstPart() {
            return firstPart;
        }

        public String getFourthPart() {
            return fourthPart;
        }

        public String getFifthPart() {
            return fifthPart;
        }

        public String getOriginalName() {
            return originalName;
        }
    }
}