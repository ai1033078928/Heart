package com.stream.kafka_source;

import com.alibaba.fastjson.JSONObject;
import com.stream.func.ToSinkFunction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;
import scala.Function0;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * Direct方式
 * 这种方法相较于Receiver方式(KafkaUtils.createStream)的优势在于：
 *
 * 1. 简化的并行：
 *      在Receiver的方式中我们提到创建多个Receiver之后利用union来合并成一个Dstream的方式提高数据传输并行度。
 *      而在Direct方式中，Kafka中的partition与RDD中的partition是一一对应的并行读取Kafka数据，
 *      这种映射关系也更利于理解和优化。
 * 2. 高效：
 *      在Receiver的方式中，为了达到0数据丢失需要将数据存入Write Ahead Log中，这样在Kafka和日志中就保存了两份数据，
 *      浪费！而第二种方式不存在这个问题，只要我们Kafka的数据保留时间足够长，我们都能够从Kafka进行数据恢复。
 * 3. 精确一次：
 *      在Receiver的方式中，使用的是Kafka的高阶API接口从Zookeeper中获取offset值，这也是传统的从Kafka中读取数据的方式，
 *      但由于Spark Streaming消费的数据和Zookeeper中记录的offset不同步，这种方式偶尔会造成数据重复消费。
 *      而第二种方式，直接使用了简单的低阶Kafka API，Offsets则利用Spark Streaming的checkpoints进行记录，消除了这种不一致性。
 */
public class KafkaToDB {

    private volatile static Broadcast<Properties> broadcast = null;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("hadoop.home.dir", "F:\\hadoop-2.9.2");

        Properties properties = new Properties();
        properties.setProperty("GROUP_ID", "test");
        properties.setProperty("RPC_NUMTHREADS", "3");
        properties.setProperty("MAX_RATE_PERPARTITION", "3");
        properties.setProperty("MAX_EXECUTORS", "2");
        properties.setProperty("DURATIONS", "10");
        properties.setProperty("KAFKA_HOST", "banana:9092");


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
        // String jassc = "org.apache.kafka.common.security.scram.ScramLoginModule required username=xxx password=xxx;";
        // kafkaParams.put("sasl.jaas.config", jassc);

        SparkConf conf = new SparkConf()
                // 本地执行使用
                .setMaster("local[1]")
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
                .registerKryoClasses((Class<?>[]) Arrays.asList(ConsumerRecord.class).toArray())
                ;

        int durations = Integer.parseInt(properties.getProperty("DURATIONS"));

        JavaSparkContext javaSparkContext = new JavaSparkContext(conf);
        System.out.println(javaSparkContext);

        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(javaSparkContext, Durations.seconds(durations));

        // 需要消费的主题
        ArrayList<String> topics = new ArrayList<>();
        topics.add("data");

        // 指定每个分区的起始偏移量
        /*Map<TopicPartition, Long> fromOffsets = new HashMap<>();
        fromOffsets.put(new TopicPartition(topics.get(0), 0), 0L);*/ // 从分区0的偏移量100开始
        // fromOffsets.put(new TopicPartition("topicA", 1), 200L); // 从分区1的偏移量200开始

        // Kafka 数据源
        JavaInputDStream<ConsumerRecord<String, String>> directStream = KafkaUtils.createDirectStream(
                javaStreamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaParams));

        System.out.println(directStream.toString());
        directStream
                .foreachRDD(rdd -> {
                    // 处理数据
                    rdd.foreachPartition(new ToSinkFunction(javaSparkContext));

                    // 处理偏移量
                    // 1.获取 offset，也可以选地方保存消费者组各个topic的offset
                    OffsetRange[] offsetRanges = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
                    // 2.调用官方API，提交消费者组的偏移量
                    CanCommitOffsets canCommitOffsets = (CanCommitOffsets) directStream.inputDStream();
                    canCommitOffsets.commitAsync(offsetRanges);

                });

        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
    }

    //加载要广播的数据，并更新广播变量
    /*private static Broadcast updateBroadCastVar(JavaSparkContext sc, Boolean blocking) {
        if (broadcast != null) {
            //删除缓存在executors上的广播副本，并可选择是否在删除完成后进行block等待
            //底层可选择是否将driver端的广播副本也删除
            broadcast.unpersist(blocking);
        }
        broadcast = sc.broadcast(fetchLastestData());
        return broadcast;
    }*/

    /*private static Properties fetchLastestData() {
        // 读取classpath下的mysql.properties并更新广播变量
        Properties mysqlProps = new Properties();
        try (InputStream input = KafkaToDB.class.getClassLoader().getResourceAsStream("mysql.properties")) {
            mysqlProps.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Broadcast<Properties> broadcastMysqlProps = javaStreamingContext.sparkContext().broadcast(mysqlProps);
        return mysqlProps;
    }*/

}
