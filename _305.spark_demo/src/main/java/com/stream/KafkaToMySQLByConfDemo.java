package com.stream;

import com.alibaba.fastjson.JSONObject;
import com.stream.entity.SparkStreamTopicConfEntity;
import com.stream.struct.DBDrivereEnum;
import com.stream.utils.DataToDB;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.HashPartitioner;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

import java.sql.*;
import java.util.*;

public class KafkaToMySQLByConfDemo {

    private volatile static Broadcast<Map<String, SparkStreamTopicConfEntity>> broadcast = null;

    /**
     * 过时 API
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("hadoop.home.dir", "F:\\hadoop-2.9.2");

        Properties properties = new Properties();
        properties.setProperty("GROUP_ID", "demo");
        properties.setProperty("RPC_NUMTHREADS", "3");
        properties.setProperty("MAX_RATE_PERPARTITION", "3");
        properties.setProperty("MAX_EXECUTORS", "2");
        properties.setProperty("DURATIONS", "10");
        properties.setProperty("KAFKA_HOST", "127.0.0.1:9092");


        Map<String, Object> kafkaParams = new HashMap();
        kafkaParams.put("bootstrap.servers", properties.getProperty("KAFKA_HOST"));
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", properties.getProperty("GROUP_ID"));
        kafkaParams.put("enable.auto.commit", false);          // 不自动提交偏移量，需手动提交
        kafkaParams.put("auto.offset.reset", "earliest");
        // kafka 带有账号密码sasl协议的认证
        // kafkaParams.put("security.protocol", "SASL_PLAINTEXT");
        // kafkaParams.put("sasl.mechanism", "SCRAM-SHA-256");
        // kafkaParams.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=xxx password=xxx;");

        SparkConf conf = new SparkConf()
                // 本地执行使用
                .setMaster("local[2]")
                .setAppName(properties.getProperty("GROUP_ID"))
                .set("spark.rpc.netty.dispatcher.numThreads", properties.getProperty("RPC_NUMTHREADS"))
                .set("spark.streaming.backpressure.enabled", "true")
                .set("spark.streaming.kafka.maxRatePerPartition", properties.getProperty("MAX_RATE_PERPARTITION"))
                .set("spark.dynamicAllocation.maxExecutors", properties.getProperty("MAX_EXECUTORS"))
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.streaming.kafka.consumer.poll.ms", "2000")
                .set("spark.task.maxFailures", "3")
                .set("spark.driver.memory", "1g")
                .set("spark.driver.extraJavaOptions", "-XX:+UseG1GC")
                .set("spark.testing.memory","2147480000")
                // .registerKryoClasses()
                ;

        int durations = Integer.parseInt(properties.getProperty("DURATIONS"));

        JavaSparkContext javaSparkContext = new JavaSparkContext(conf);

        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(javaSparkContext, Durations.seconds(durations));

        // 需要消费的主题
        ArrayList<String> topics = new ArrayList<>();
        topics.add("topicA");
        topics.add("topicB");

        // 指定每个分区的起始偏移量
        Map<TopicPartition, Long> fromOffsets = new HashMap<>();
        fromOffsets.put(new TopicPartition("topicA", 0), 100L); // 从分区0的偏移量100开始
        fromOffsets.put(new TopicPartition("topicA", 1), 200L); // 从分区1的偏移量200开始

        // Kafka 数据源
        JavaInputDStream<ConsumerRecord<String, String>> directStream = KafkaUtils.createDirectStream(
                javaStreamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaParams, fromOffsets));

        directStream
                .mapToPair(new PairFunction<ConsumerRecord<String, String>, String, JSONObject>() {
                    @Override
                    public Tuple2<String, JSONObject> call(ConsumerRecord<String, String> record) throws Exception {
                        // {"key", "1", "value": "11111111111111111"}
                        JSONObject jsonObject = JSONObject.parseObject(record.value());
                        String key = jsonObject.getString("key");
                        return new Tuple2<>(key, jsonObject);
                    }
                })
                .filter(new Function<Tuple2<String, JSONObject>, Boolean>() {
                    @Override
                    public Boolean call(Tuple2<String, JSONObject> v1) throws Exception {
                        Map<String, SparkStreamTopicConfEntity> value = broadcast.getValue();
                        if (null == value.get(v1._1)) {
                            return false;
                        }
                        return true;
                    }
                })
                .groupByKey()
                .foreachRDD(rdd -> {
                    long count = rdd.count();
                    int parts = (count / 1000) + 1 > 5 ? 5 : (int) (count / 1000) + 1;
                    rdd.partitionBy(new HashPartitioner(parts))
                            .foreachPartition(new DataToDB(javaSparkContext));
                    updateBroadCastVar(javaSparkContext, true);
                })
        ;

        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
    }

    //加载要广播的数据，并更新广播变量
    private static void updateBroadCastVar(JavaSparkContext sc, Boolean blocking) {
        if (broadcast != null) {
            //删除缓存在executors上的广播副本，并可选择是否在删除完成后进行block等待
            //底层可选择是否将driver端的广播副本也删除
            broadcast.unpersist(blocking);
        }
        broadcast = sc.broadcast(fetchLastestData());
    }

    private static Map<String, SparkStreamTopicConfEntity> fetchLastestData() {
        Map<String, SparkStreamTopicConfEntity> res = new HashMap<>();
        String url = "jdbc:mysql://localhost:3306/test";
        Properties dbProp = new Properties();
        dbProp.put("user", "root");
        dbProp.put("password", "root");
        String sql = "select topic, table_name from SparkStreamTopic";

        String driverStr = null;
        DBDrivereEnum[] values = DBDrivereEnum.values();
        for (DBDrivereEnum value : values) {
            if (url.startsWith(value.getUrlPrefix())) {
                driverStr = value.getDriverClass();
                break;
            }
        }


        if (null != driverStr) {
            // 获取Driver实现类对象，使用反射
            Class<?> clazz = null;
            Driver driver = null;
            try {
                clazz = Class.forName(driverStr);
                driver = (Driver) clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // 获取连接
            try (Connection conn = driver.connect(url, dbProp);
                 PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    SparkStreamTopicConfEntity entity = SparkStreamTopicConfEntity
                            .builder()
                            .topic(resultSet.getString("topic"))
                            .tableName(resultSet.getString("table_name"))
                            .build();
                    res.put(entity.getTableName(), entity);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // throw new RuntimeException(e);
            }
        }
        return res;
    }

}
