package com.windows;

import com.jobs.bean.User;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class WindowsApi_2_ProcessDemo {
    /**
     * aggregate(AggregateFunction<T, ACC, V> aggFunction, ProcessWindowFunction<V, R, K, W> windowFunction)
     * reduce(ReduceFunction<T> reduceFunction, ProcessWindowFunction<T, R, K, W> function)
     * 结合两者的优点
     * 1. 增量聚合：来一条计算一条，存储中间计算结果，占用空间少
     * 2. 全窗口函数：通过上下文实现灵活的功能
     */

    public static void main(String[] args) throws Exception {

//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> socketTextStream = env.socketTextStream("172.24.199.32", 7777);

        KeyedStream<User, String> keyed = socketTextStream
                .map(new MapFunction<String, User>() {
                    @Override
                    public User map(String value) throws Exception {
                        String[] split = value.split(",");
                        return new User(split[0], Integer.valueOf(split[1]), Integer.valueOf(split[2]));
                    }
                })
                .keyBy(User::getName);

        // 1. 窗口分配器
        WindowedStream<User, String, TimeWindow> window = keyed.window(TumblingProcessingTimeWindows.of(Duration.ofSeconds(10)));

        // 2. 窗口函数
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
