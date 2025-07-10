package com.sql;

import com.sql.pojo.User;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.ReduceFunction;
import org.apache.spark.sql.*;
import scala.Tuple2;

public class SqlFuncDemo {
    public static void main(String[] args) {

        // 创建配置对象
        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("SparkSqlDemo");

        // 创建 SparkSession
        SparkSession sparkSession = SparkSession.builder()
                .config(sparkConf)
                .getOrCreate();

        // 读取数据
        Dataset<Row> json = sparkSession.read().json("D:\\Program Files\\JetBrains\\project\\heart\\_305.spark_demo\\input\\user.json");

        // 转换为类和对象
        // -- 1.bean
        Dataset<User> userDataset = json.as(Encoders.bean(User.class));
        // -- 2.map
        /*Dataset<User> userDataset = json.map(
                new MapFunction<Row, User>() {
                    @Override
                    public User call(Row value) throws Exception {
                        return new User(value.getString(1), value.getLong(0));
                    }
                },
                Encoders.bean(User.class)  // 使用kryo在底层会有部分算子无法使用
        );*/

        System.out.println("原数据：");
        userDataset.show();

        // 排序
        System.out.println("名称排序：");
        Dataset<User> name = userDataset.sort("name");
        name.show();

        // 计数
        System.out.println("计数：");
        long count = userDataset.count();
        System.out.println(count);


        // 分组聚合
        System.out.println("分组：");
        KeyValueGroupedDataset<String, User> groupByKey = userDataset.groupByKey(
                new MapFunction<User, String>() {
                    @Override
                    public String call(User value) throws Exception {
                        return value.name;
                    }
                },
                Encoders.STRING()
        );
        System.out.println(groupByKey.toString());

        System.out.println("聚合：");
        Dataset<Tuple2<String, User>> reduceGroups = groupByKey.reduceGroups(
                new ReduceFunction<User>() {
                    @Override
                    public User call(User v1, User v2) throws Exception {
                        return new User(v1.getName(), Math.max(v1.getAge(), v2.getAge()));
                    }
                }
        );
        reduceGroups.show();

        // 关闭 SparkSession
        sparkSession.close();
    }
}
