package com.jobs.transformation;

import com.jobs.bean.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Random;

public class AggregationDemo {
    public static void main(String[] args) throws Exception {
        /**
         * 聚合算子学习  sum、max、maxBy、min、minBy、reduce
         * 1. 数据源类型需指定为Types.POJO(User.class)而不是Types.GENERIC(User.class)；max、sum不支持GENERIC
         */

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        Random random = new Random();

        DataGeneratorSource<User> dataGeneratorSource = new DataGeneratorSource<User>(new GeneratorFunction<Long, User>() {
            @Override
            public User map(Long aLong) throws Exception {
                return new User("name-" + aLong, random.nextInt(2), Integer.valueOf(String.valueOf(aLong)));
            }
        },
                10,
                RateLimiterStrategy.perSecond(2),
                Types.POJO(User.class)   // 必须指定为POJO，否则后续sum、max不支持该类型
        );

        KeyedStream<User, String> aggregationKeyedStream = env.fromSource(dataGeneratorSource, WatermarkStrategy.noWatermarks(), "AggregationDemo")
                .keyBy(new KeySelector<User, String>() {
                    @Override
                    public String getKey(User user) throws Exception {
                        return String.valueOf(user.sex);
                    }
                });

        aggregationKeyedStream.print("生成数据：");

        aggregationKeyedStream.sum("age").print("年龄求和：");
        aggregationKeyedStream.max("age").print("年龄最大值：");                 // 其他字段取第一条的
        aggregationKeyedStream.maxBy("age").print("年龄最大值2：");   // 其他字段取最大一条的
        aggregationKeyedStream.min("age").print("年龄最小值：");                  // 其他字段取第一条的
        aggregationKeyedStream.minBy("age").print("年龄最小值2：");   // 其他字段取最小一条的


        aggregationKeyedStream.reduce(new ReduceFunction<User>() {
            @Override
            public User reduce(User user, User t1) throws Exception {
                user.age = user.age + t1.age;
                return user;
            }
        }).print("reduce");

        env.execute();
    }
}
