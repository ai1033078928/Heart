package com.watermark;

import com.jobs.bean.User;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class WaterMarkApi_3_HowToPassDemo2 {
    /**
     *
     */

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        /**
         * 多并行度下的watermark传递
         * 1. 上游多个并行度，取最小
         * 2. 往下游发送，广播
         *
         * 问题：上游一个分区如果一直没来数据，则watermark一直不会变
         * 设置空闲等待时间解决
         */
        env.setParallelism(2);
        env.getConfig().setAutoWatermarkInterval(200);

        DataStreamSource<String> socketTextStream = env.socketTextStream("172.24.207.93", 7777);

        // 指定watermark策略
        WatermarkStrategy<User> watermarkStrategy = WatermarkStrategy
                // 1.1 乱序的流，有等待时间
                .<User>forGenerator((WatermarkGeneratorSupplier<User>) context -> new MyWaterMarkGenerator<>(3000L))
                // 1.2 指定时间戳分配器，从数据中提取
                .withTimestampAssigner(
                        (SerializableTimestampAssigner<User>) (element, recordTimestamp) -> {
                            // 返回的时间戳，毫秒数
                            System.out.println("数据：" + element.toString() + " recordTimestamp:" + recordTimestamp);
                            return element.getAge() * 1000L;  // 假设是时间
                        }
                )
                // TODO 1.3 设置空闲等待时间；此为现实时间而非事件事件
                .withIdleness(Duration.ofSeconds(20));

        KeyedStream<User, String> keyed = socketTextStream
                .map((MapFunction<String, User>) value -> {
                    String[] split = value.split(",");
                    return new User(split[0], Integer.valueOf(split[1]), Integer.valueOf(split[2]));
                })
                .partitionCustom(
                        new Partitioner<User>() {
                            @Override
                            public int partition(User key, int numPartitions) {
                                return key.getAge() % numPartitions;
                            }
                        },
                        new KeySelector<User, User>() {
                            @Override
                            public User getKey(User value) throws Exception {
                                return value;
                            }
                        }
                )
                .assignTimestampsAndWatermarks(watermarkStrategy)
                .keyBy(User::getName);


        // 窗口分配器（事件时间语句窗口）
        WindowedStream<User, String, TimeWindow> window = keyed.window(TumblingEventTimeWindows.of(Duration.ofSeconds(10)));

        // 窗口函数
        SingleOutputStreamOperator<String> process = window.process(new ProcessWindowFunction<User, String, String, TimeWindow>() {
            /**
             * 包含所有apply功能；全窗口函数计算逻辑，只在窗口触发式调用一次
             * @param s          分组key
             * @param context    上下文
             * @param iterable   存的数据
             * @param collector  采集器
             * @throws Exception
             */
            @Override
            public void process(String s, ProcessWindowFunction<User, String, String, TimeWindow>.Context context, Iterable<User> iterable, Collector<String> collector) throws Exception {
                TimeWindow window = context.window();
                long start = window.getStart();
                long end = window.getEnd();
                String windowStart = DateFormatUtils.format(start, "yyyy-MM-dd HH:mm:ss.SSS");
                String windowEnd = DateFormatUtils.format(end, "yyyy-MM-dd HH:mm:ss.SSS");
                long count = iterable.spliterator().estimateSize();
                collector.collect("key=" + s + "的窗口[" + windowStart + "," + windowEnd + ")包含" + count + "条数据===>" + iterable.toString());
            }
        });

        process.print("窗口结果：");

        env.execute();

    }
}
