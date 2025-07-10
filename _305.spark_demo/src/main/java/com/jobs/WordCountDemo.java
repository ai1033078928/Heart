package com.jobs;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WordCountDemo {

    public static void main(String[] args) throws InterruptedException {

        // 创建配置对象
        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("wc");

        // 创建上下文
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        JavaRDD<String> data = sc.parallelize(Arrays.asList("hello world", "hello spark", "hello java"));

        JavaPairRDD<String, Integer> reduce = data.flatMap(
                        new FlatMapFunction<String, String>() {

                            @Override
                            public Iterator<String> call(String s) throws Exception {
                                String[] split = s.split(" ");
                                return Arrays.asList(split).iterator();
                            }
                        }
                )
                .filter(x -> !x.equals("java"))
                .mapToPair(x -> Tuple2.apply(x, 1))
                .reduceByKey(
                        new Function2<Integer, Integer, Integer>() {
                            @Override
                            public Integer call(Integer v1, Integer v2) throws Exception {
                                return Integer.sum(v1, v2);
                            }
                        }
                )
                /*.reduceByKey(Integer::sum)*/;

        reduce.collect().forEach(System.out::println);


        // Spark web UI：http://DESKTOP-78BHFCM.mshome.net:4040  localhost:4040
        // Thread.sleep(20000);

        // 关闭sc
        sc.stop();

    }

}
