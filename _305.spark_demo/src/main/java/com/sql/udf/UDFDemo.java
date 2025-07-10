package com.sql.udf;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;

import static org.apache.spark.sql.functions.udf;

public class UDFDemo {
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

        // 定义函数
        // 需要首先导入依赖 import static org.apache.spark.sql.functions.udf;
        UserDefinedFunction hello_udf = udf(
                new UDF1<String, String>() {
                    @Override
                    public String call(String s) throws Exception {
                        return "hello_" + s;
                    }
                },
                DataTypes.StringType
        );

        // 注册函数
        sparkSession.udf().register("h1", hello_udf);

        // 支持所有的hive sql语法,并且会使用spark的优化
        sparkSession
                .sql("select h1(name) as NewName, age from user order by name")
                .show(100);

        sparkSession.close();
    }
}
