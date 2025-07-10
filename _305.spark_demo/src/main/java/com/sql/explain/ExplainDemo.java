package com.sql.explain;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;

public class ExplainDemo {

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
        String sql_str1 = "select name\n" +
                "       ,age\n" +
                "       ,row_number() over(partition by name order by age desc) as rn\n" +
                "       ,sum(age) over(partition by name) as sum_age\n" +
                "  from user" +
                "  where name <> 'xxx'";

        String sql_str = "select name, max(age) as max_age from user group by name having max_age/*max(age)*/ >= 19 order by name";

        /*sparkSession
                .sql(sql_str)
                .show(100);*/

        System.out.println("=====================================explain()-只展示物理执行计划============================================");
        sparkSession.sql(sql_str).explain();

        System.out.println("===============================explain(mode = \"simple\")-只展示物理执行计划=================================");
        sparkSession.sql(sql_str).explain("simple");

        System.out.println("============================explain(mode = \"extended\")-展示逻辑和物理执行计划==============================");
        sparkSession.sql(sql_str).explain("extended");

        System.out.println("============================explain(mode = \"codegen\")-展示可执行java代码===================================");
        sparkSession.sql(sql_str).explain("codegen");

        System.out.println("============================explain(mode = \"cost\")-展示可执行java代码===================================");
        sparkSession.sql(sql_str).explain("cost");

        System.out.println("============================explain(mode = \"formatted\")-展示格式化的物理执行计划=============================");
        sparkSession.sql(sql_str).explain("formatted");


        sparkSession.close();
    }
}
