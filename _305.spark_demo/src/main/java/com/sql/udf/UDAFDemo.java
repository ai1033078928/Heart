package com.sql.udf;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.expressions.Aggregator;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.types.DataTypes;

import java.io.Serializable;

import static org.apache.spark.sql.functions.udaf;

public class UDAFDemo {
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
        UserDefinedFunction avg_udaf = udaf(
                new Aggregator<Long, Buffer, Double>() {

                    @Override
                    public Buffer zero() {
                        return new Buffer(0L, 0L);
                    }

                    @Override
                    public Buffer reduce(Buffer b, Long a) {
                        return new Buffer(Long.sum(b.getSum(), a), Long.sum(b.getCount(), 1L));
                    }

                    @Override
                    public Buffer merge(Buffer b1, Buffer b2) {
                        return new Buffer(Long.sum(b1.getSum(), b2.getSum()), Long.sum(b1.getCount(), b2.getCount()));
                    }

                    @Override
                    public Double finish(Buffer reduction) {
                        return Double.valueOf(reduction.getSum() / reduction.getCount());
                    }

                    @Override
                    public Encoder<Buffer> bufferEncoder() {
                        // 可以用kryo进行优化
                        return Encoders.kryo(Buffer.class);
                    }

                    @Override
                    public Encoder<Double> outputEncoder() {
                        return Encoders.DOUBLE();
                    }
                },
                Encoders.LONG()
        );

        // 注册函数
        sparkSession.udf().register("avg1", avg_udaf);

        // 支持所有的hive sql语法,并且会使用spark的优化
        sparkSession
                .sql("select * from user order by name")
                .show(100);
        sparkSession
                .sql("select name as NewName, avg1(age) as avg_age from user group by name order by name")
                .show(100);

        String sql_str = "select name\n" +
                "       ,age\n" +
                "       ,row_number() over(partition by name order by age desc) as rn\n" +
                "       ,sum(age) over(partition by name) as sum_age\n" +
                "  from user";
        sparkSession
                .sql(sql_str)
                .show(100);

        sparkSession.close();
    }

    public static class Buffer implements Serializable {
        public Long sum;
        public Long count;

        public Buffer() {
        }

        public Buffer(Long sum, Long count) {
            this.sum = sum;
            this.count = count;
        }

        public Long getSum() {
            return sum;
        }

        public void setSum(Long sum) {
            this.sum = sum;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return "Buffer{" +
                    "sum=" + sum +
                    ", count=" + count +
                    '}';
        }
    }
}
