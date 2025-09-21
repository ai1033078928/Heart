package com;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class Flink_CDCWithCustomerSchema {

    /**
     * TODO 从kafka解析数据
     *
     * 解决问题 com.mysql.cj.exceptions.UnableToConnectException: Public Key Retrieval is not allowed
     * 方案1：
     * ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
     * ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
     * FLUSH PRIVILEGES;
     */
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.put("sourceIP", "127.0.0.1");
        properties.put("sourcePort", "3306");
        properties.put("sourceUser", "root");
        properties.put("sourcePassWD", "root");
        properties.put("sourceDBs", "test2");
        properties.put("sourceTables", "test2.test_user");
        properties.put("timeZone", "Asia/Shanghai");
        properties.put("kafkaServers", "8.8.8.10:9092");
        properties.put("kafkaTopic", "data");

        Properties jdbcProp = new Properties();
        jdbcProp.put("useUnicode", "true");
        jdbcProp.put("characterEncoding", "UTF-8");

        //1.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        // StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        //2.创建 Flink-MySQL-CDC 的 Source
        MySqlSource<String> mysqlSource = MySqlSource.<String>builder()
                .hostname(properties.getProperty("sourceIP"))
                .port(Integer.parseInt(properties.getProperty("sourcePort")))
                .username(properties.getProperty("sourceUser"))
                .password(properties.getProperty("sourcePassWD"))
                .databaseList(properties.getProperty("sourceDBs").split(","))
                .tableList(properties.getProperty("sourceTables").split(","))
                .serverTimeZone(properties.getProperty("timeZone"))
                .scanNewlyAddedTableEnabled(true)                   // 启用扫描新添加的表功能
                .jdbcProperties(jdbcProp)
                .includeSchemaChanges(true)                         // 包含 ddl 变更
                .startupOptions(StartupOptions.latest())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();

        // 设置 3s 的 checkpoint 间隔
        // env.enableCheckpointing(3000);

        // 输出到Kafka
        KafkaSink<String> kafkaSink = KafkaSink
                .<String>builder()
                // kafka地址端口
                .setBootstrapServers(properties.getProperty("kafkaServers"))
                // 指定序列化器
                .setRecordSerializer(
                        KafkaRecordSerializationSchema
                                .<String>builder()
                                .setTopic(properties.getProperty("kafkaTopic"))
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .build()
                )
                // 一致性级别
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                // 如果精准一次，必须设置事务前缀
                .setTransactionalIdPrefix("flink-kafka-")
                // 如果精准一次，必须设置事务超时时间  大于checkpoint，小于15min
                .setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, String.valueOf(10*60*1000))
                .build();

        //3.使用 CDC Source 从 MySQL 读取数据
        env.fromSource(mysqlSource, WatermarkStrategy.noWatermarks(), "mysqlSource")
                .addSink(new ConsoleSink())
                //.sinkTo(kafkaSink)
        ;

        //5.执行任务
        env.execute("MySQL CDC Example");
    }


    /**
     * 写 Log 到控制台
     */
    public static class ConsoleSink extends RichSinkFunction<String> {
        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
        }

        @Override
        public void close() throws Exception {
            super.close();
        }

        @Override
        public void invoke(String value, Context context) throws Exception {
            System.out.println(value);
        }
    }

}

