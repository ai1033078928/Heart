package com.sql.kafka;

import org.apache.spark.sql.SparkSession;

import java.util.ArrayList;

public class KafkaSink {

    public static void main(String[] args) {

        SparkSession sparkSession = SparkSession.builder()
                .master("local[2]")
                .appName("kafkaSource")
                .getOrCreate();

        ArrayList<String> data = new ArrayList<>();
        data.add("111111111");
        data.add("222222222");
        data.add("333333333");

        sparkSession.createDataFrame(data, String.class)
                .toDF("value")
                .write()
                .format("kafka")
                .option("kafka.bootstrap.servers", "8.8.8.10:9092")
                .option("subscribe", "test")
                .save();


    }
}
