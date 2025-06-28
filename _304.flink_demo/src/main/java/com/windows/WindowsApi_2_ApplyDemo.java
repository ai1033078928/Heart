package com.windows;

import com.jobs.bean.User;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class WindowsApi_2_ApplyDemo {

    /**
     * apply弃用，process包含它的功能且更强大
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
        SingleOutputStreamOperator<String> aggregate = window.apply(new WindowFunction<User, String, String, TimeWindow>() {

            /**
             * @param s   分组的key
             * @param timeWindow  窗口对象
             * @param iterable    存的数据
             * @param collector   采集器
             * @throws Exception
             */
            @Override
            public void apply(String s, TimeWindow timeWindow, Iterable<User> iterable, Collector<String> collector) throws Exception {

            }
        });

        aggregate.print("窗口结果：");

        env.execute();

    }
}
