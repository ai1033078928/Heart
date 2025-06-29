package com.watermark;

import com.jobs.bean.User;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.time.Duration;

public class WaterMarkApi_4_AllowLatenessDemo {
    /**
     * 1. 乱序和迟到的区别
     *      乱序  时间小的数据比时间大的数据晚来
     *      迟到  数据时间 < watermark
     *
     * 2. 乱序、迟数据处理
     *      1 watermark中指定乱序等待时间
     *      2 如果开窗，设置窗口允许迟到
     *          => 推迟关窗时间，关窗前，数据来一条重新计算一次
     *          => 关窗后，迟到数据不会计算
     *      3 关窗后迟到数据，放入到侧输出流
     *
     * 关窗时间（最大事件时间 16s） = 窗口最大时间(10s) + 乱序等待时间(3s) - 1ms + 允许迟到时间(3s)
     *
     * TODO 设置经验
     * 1. watermark设置一个不算特别大的，一般是秒级，在 乱序和延迟 取舍
     * 2. 设置一定的窗口允许迟到，只考虑大部分的迟到数据，太极端的不管
     * 3. 极端的小部分迟到数据，放到侧输出流，后续再做处理
     */

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> socketTextStream = env.socketTextStream("172.24.204.131", 7777);

        // 指定watermark策略
        WatermarkStrategy<User> watermarkStrategy = WatermarkStrategy
                // 1.1 乱序的流，有等待时间
                .<User>forBoundedOutOfOrderness(Duration.ofSeconds(3))
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


        OutputTag<User> outputTag = new OutputTag<>("later-data", Types.POJO(User.class));
        // 窗口分配器（事件时间语句窗口）
        WindowedStream<User, String, TimeWindow> window = keyed
                .window(TumblingEventTimeWindows.of(Duration.ofSeconds(10)))
                // TODO 允许迟到到来之后再次触发计算，此时间后才关窗
                .allowedLateness(Duration.ofSeconds(3))
                // TODO 最后关窗之后的数据放到侧输出流
                .sideOutputLateData(outputTag);

        // 窗口函数
        SingleOutputStreamOperator<String> process = window
                .process(new ProcessWindowFunction<User, String, String, TimeWindow>() {
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

        // 从主流获取到测流数据，并打印出来
        process.getSideOutput(outputTag).printToErr("关窗后迟到数据：");


        env.execute();

    }
}
