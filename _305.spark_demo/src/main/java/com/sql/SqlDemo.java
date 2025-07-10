package com.sql;

import com.sql.pojo.User;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SqlDemo {
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

        // 创建视图 => 转换为表格 填写表名
        userDataset.createOrReplaceTempView("user");

        // 支持所有的hive sql语法,并且会使用spark的优化
        sparkSession
                .sql("select * from user order by name")
                .show(100);

        sparkSession
                .sql("select name, max(age) as max_age from user group by name having max_age/*max(age)*/ >= 19 order by name")
                .show(100);


        sparkSession.close();
    }
}
