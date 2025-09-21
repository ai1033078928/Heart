package com.sql.kafka;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class KafkaSource {
    public static void main(String[] args) {

        SparkSession sparkSession = SparkSession.builder()
                .master("local[2]")
                .appName("kafkaSource")
                .getOrCreate();

        Dataset<Row> dataset = sparkSession.read()
                .format("kafka")
                .option("kafka.bootstrap.servers", "8.8.8.10:9092")
                .option("subscribe", "test")
                .load();


        dataset.printSchema();

        dataset.selectExpr("CAST(key AS STRING)",
                "CAST(value AS STRING)","topic")
                .show(false);
    }
}
