package com.sql;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import static org.apache.spark.sql.functions.col;

public class DSLDemo {
    public static void main(String[] args) {

        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("sparksqldemo2");

        SparkSession sparkSession = SparkSession
                .builder()
                .config(sparkConf)
                .getOrCreate();

        // 读取数据
        Dataset<Row> userDataset = sparkSession
                .read()
                .json("D:\\Program Files\\JetBrains\\project\\heart\\_305.spark_demo\\input\\user.json")
                /*.as(Encoders.bean(User.class))*/;

        userDataset
                .select(col("name").as("NewName"), col("age").plus(100).name("age+100"))
                .filter(col("age").gt(18))
                .orderBy("name")
                .show();


        sparkSession.close();
    }
}
