package com.jobs.transformation;

import com.jobs.bean.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Random;

public class PartitionDemo {
    public static void main(String[] args) throws Exception {
        /**
         * 随机分区(shuffle)    this.random.nextInt(this.numberOfChannels)
         * 轮询分区(Round-Robin)   this.nextChannelToSendTo = (this.nextChannelToSendTo + 1) % this.numberOfChannels;
         * 重缩放分区(rescale)
         * 广播(broadcast)
         * 全局分区(global)   return 0; 发往第一个分区
         * 自定义分区(Custom)  自定义分区逻辑
         */
        // StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(2);

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

        env.fromSource(dataGeneratorSource, WatermarkStrategy.noWatermarks(), "dataGeneratorSource")
//                .shuffle()  // 随机
//                .rebalance()  // 轮询
//                .rescale()  // 分组(小范围)轮询
//                .broadcast()  // 每个分区广播
//                .global()  // 全局，发往第一个分区
                .partitionCustom(
                        new Partitioner<User>() {
                                 @Override
                                 public int partition(User user, int numPartitions) {
                                     return user.sex % numPartitions;
                                 }
                             },
                        new KeySelector<User, User>() {
                            @Override
                            public User getKey(User user) throws Exception {
                                return user;
                            }
                        })
                .print();



        env.execute();
    }
}
