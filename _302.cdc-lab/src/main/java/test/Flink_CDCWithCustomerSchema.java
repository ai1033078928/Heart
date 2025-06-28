package test;

import com.alibaba.fastjson.JSONObject;

import com.ververica.cdc.connectors.mysql.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Properties;

public class Flink_CDCWithCustomerSchema {
    public static void main(String[] args) throws Exception {
        //1.创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);


        //2.创建 Flink-MySQL-CDC 的 Source
        DebeziumSourceFunction<String> mysqlSource = MySqlSource.<String>builder()
                .hostname("127.0.0.1")
                .port(3306)
                .username("root")
                .password("root")
                .databaseList("test2")
                .serverTimeZone("Asia/Shanghai")
                .startupOptions(StartupOptions.latest())
                .deserializer(new DebeziumDeserializationSchema<String>() { //自定义数据解析器
                    @Override
                    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {
                        // 获 取 主 题 信 息 , 包 含 着 数 据 库 和 表 名
                        String topic = sourceRecord.topic();
                        String[] arr = topic.split("\\.");
                        String db = arr[1];
                        String tableName = arr[2];
                        //获取操作类型 READ DELETE UPDATE CREATE
                        Envelope.Operation operation =  Envelope.operationFor(sourceRecord);
                        //获取值信息并转换为 Struct 类型
                        Struct value = (Struct) sourceRecord.value();

                        /*
                        //获取变化后的数据
                        Struct after = value.getStruct("after");
                        //创建 JSON 对象用于存储数据信息
                        JSONObject data = new JSONObject();
                        if (after != null) {
                            Schema schema = after.schema();
                            for (Field field : schema.fields()) {
                                data.put(field.name(), after.get(field.name()));
                            }
                        }
                        //创建 JSON 对象用于封装最终返回值数据信息
                        JSONObject result = new JSONObject();
                        result.put("operation", operation.toString().toLowerCase());
                        result.put("data", data);
                        result.put("database", db);
                        result.put("table", tableName);
                        //发送数据至下游
                        collector.collect(result.toJSONString());
                        */
                        collector.collect(sourceRecord.toString());
                        collector.collect(value.toString());

                    }
                    @Override
                    public TypeInformation<String> getProducedType() {
                        return TypeInformation.of(String.class);
                    }
                })
                .build();

        //3.使用 CDC Source 从 MySQL 读取数据
        // DataStreamSource<String> mysqlDS = env.addSource(mysqlSource);
        // mysqlDS.print()
        env.addSource(mysqlSource).addSink(new ConsoleSink());
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

