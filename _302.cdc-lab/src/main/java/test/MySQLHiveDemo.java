package test;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.hive.HiveCatalog;

import java.util.concurrent.ExecutionException;

public class MySQLHiveDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建 Flink Table 环境
        /*EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inStreamingMode()
                .build();*/
        EnvironmentSettings settings = EnvironmentSettings.inStreamingMode();

        TableEnvironment tableEnv = TableEnvironment.create(settings);

        // 注册 HiveCatalog
        /*String name = "myhive";
        String defaultDatabase = "default";
        String hiveConfDir = "D:\\Program Files\\JetBrains\\project\\heart\\_304.flink_demo\\src\\main\\resources"; // 替换为你的 hive-site.xml 路径
        String version = "2.3.6"; // 你的 Hive 版本

        HiveCatalog hive = new HiveCatalog(name, defaultDatabase, hiveConfDir, version);
        tableEnv.registerCatalog(name, hive);*/

        // 切换 Catalog
        /*tableEnv.useCatalog(name);*/

        // 创建 MySQL 源表（Flink CDC）
        tableEnv.executeSql(
                "CREATE TABLE if not exists mysql_source (\n" +
                        "  id INT,\n" +
                        "  name STRING,\n" +
                        "  age INT,\n" +
                        "  PRIMARY KEY (id) NOT ENFORCED\n" +
                        ") WITH (\n" +
                        "  'connector' = 'mysql-cdc',\n" +
                        "  'hostname' = 'localhost',\n" +
                        "  'port' = '3306',\n" +
                        "  'username' = 'root',\n" +
                        "  'password' = 'root',\n" +
                        "  'database-name' = 'test2',\n" +
                        "  'table-name' = 'test'\n" +
                        ")"
        );

        // 创建 Hive 目标表（确保已在 Hive 中存在）
        /*tableEnv.executeSql(
                "CREATE TABLE IF NOT EXISTS hive_sink (\n" +
                        "  id INT,\n" +
                        "  name STRING,\n" +
                        "  age INT\n" +
                        ")\n" +
                        "PARTITIONED BY (dt STRING) " +
                        "STORED AS PARQUET"
        );*/


        tableEnv.executeSql(
                "CREATE TABLE print_sink (\n" +
                        "    id INT,\n" +
                        "    name STRING,\n" +
                        "    age INT,\n" +
                        "    dt STRING\n" +
                        ") WITH (\n" +
                        "    'connector' = 'print'\n" +
                        ")"
        );




        // 3执行写入操作
        // cdc 不支持没有 changelog 的sink
        // 方案：写入kafka、hbase等
        /*TableResult tableResult = tableEnv.executeSql(
                "INSERT INTO hive_sink\n" +
                        "SELECT id, name, age, DATE_FORMAT(CURRENT_TIMESTAMP, 'yyyy-MM-dd') as dt\n" +
                        "FROM mysql_source"
        );*/
        TableResult tableResult = tableEnv.executeSql(
                "INSERT INTO print_sink\n" +
                        "SELECT id, name, age, DATE_FORMAT(CURRENT_TIMESTAMP, 'yyyy-MM-dd') as dt\n" +
                        "FROM mysql_source"
        );

        // tableResult.getJobClient().get().getJobExecutionResult().get();
    }

}
