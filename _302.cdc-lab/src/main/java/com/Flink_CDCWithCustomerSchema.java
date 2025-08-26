package com;

import com.alibaba.fastjson.JSONObject;

import com.utils.StructToJsonConverter;
import com.ververica.cdc.connectors.mysql.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Properties;

public class Flink_CDCWithCustomerSchema {
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


        //1.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        // StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);


        //2.创建 Flink-MySQL-CDC 的 Source
        DebeziumSourceFunction<String> mysqlSource = MySqlSource.<String>builder()
                .hostname(properties.getProperty("sourceIP"))
                .port(Integer.parseInt(properties.getProperty("sourcePort")))
                // .scanNewlyAddedTableEnabled(true) // 启用扫描新添加的表功能
                .username(properties.getProperty("sourceUser"))
                .password(properties.getProperty("sourcePassWD"))
                .databaseList(properties.getProperty("sourceDBs").split(","))
                .tableList(properties.getProperty("sourceTables").split(","))
                .serverTimeZone(properties.getProperty("timeZone"))
                .startupOptions(StartupOptions.latest())
                .deserializer(new JsonDebeziumDeserializationSchema())
                /*.deserializer(new DebeziumDeserializationSchema<String>() { //自定义数据解析器
                    @Override
                    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {
                        *//*
                        // 获取主题信息, 包含着数据库和表名
                        String topic = sourceRecord.topic();
                        String[] arr = topic.split("\\.");
                        String db = arr[1];
                        String tableName = arr[2];
                        //获取操作类型 READ DELETE UPDATE CREATE
                        Envelope.Operation operation =  Envelope.operationFor(sourceRecord);
                        *//*
                        //获取值信息并转换为 Struct 类型
                        Struct value = (Struct) sourceRecord.value();
                        String jsonStr = StructToJsonConverter.toJson(value);
                        collector.collect(jsonStr);
                    }
                    @Override
                    public TypeInformation<String> getProducedType() {
                        return TypeInformation.of(String.class);
                    }
                })*/
                .build();

        // 设置 3s 的 checkpoint 间隔
        // env.enableCheckpointing(3000);

        // 输出到Kafka
        KafkaSink<String> sink = KafkaSink
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
        // env.addSource(mysqlSource).addSink(new ConsoleSink());
        env.addSource(mysqlSource).sinkTo(sink);   // kafka sink
        //5.执行任务
        env.execute("MySQL CDC Example");
    }


    /**
     * 写 Log 到控制台
     */
    public static class ConsoleSink extends RichSinkFunction<String> {

        public void open(Configuration parameters) throws Exception {
        }

        public void invoke(String value, Context context) throws Exception {
            System.out.println(value);
        }
    }

}

