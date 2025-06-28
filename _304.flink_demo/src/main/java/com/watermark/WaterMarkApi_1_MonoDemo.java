package com.watermark;

import com.jobs.bean.User;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class WaterMarkApi_1_MonoDemo {
    /**
     *
     */

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> socketTextStream = env.socketTextStream("172.24.207.93", 7777);

        // 指定watermark策略  有序自增流
        WatermarkStrategy<User> watermarkStrategy = WatermarkStrategy
                // 1.1 升序的watermark，没有等待时间
                .<User>forMonotonousTimestamps()
                // 1.2 指定时间戳分配器，从数据中提取
                .withTimestampAssigner(new SerializableTimestampAssigner<User>() {
                    @Override
                    public long extractTimestamp(User element, long recordTimestamp) {
                        // 返回的时间戳，毫秒数
                        System.out.println("数据：" + element.toString() + " recordTimestamp:" + recordTimestamp);
                        return element.getAge() * 1000L;  // 假设是时间
                    }
                });

        KeyedStream<User, String> keyed = socketTextStream
                .map(new MapFunction<String, User>() {
                    @Override
                    public User map(String value) throws Exception {
                        String[] split = value.split(",");
                        return new User(split[0], Integer.valueOf(split[1]), Integer.valueOf(split[2]));
                    }
                })
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
