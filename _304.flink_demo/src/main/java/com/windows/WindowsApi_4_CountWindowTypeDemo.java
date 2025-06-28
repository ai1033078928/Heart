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
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SessionWindowTimeGapExtractor;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class WindowsApi_4_CountWindowTypeDemo {

    public static void main(String[] args) throws Exception {

//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> socketTextStream = env.socketTextStream("172.24.207.93", 7777);

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
        // 滚动窗口
        // WindowedStream<User, String, GlobalWindow> window = keyed.countWindow(5);
        // 滑动窗口
        WindowedStream<User, String, GlobalWindow> window = keyed.countWindow(5, 2);


        // 2. 窗口函数

        SingleOutputStreamOperator<String> process = window.process(new ProcessWindowFunction<User, String, String, GlobalWindow>() {
            @Override
            public void process(String s, ProcessWindowFunction<User, String, String, GlobalWindow>.Context context, Iterable<User> iterable, Collector<String> collector) throws Exception {
                GlobalWindow window1 = context.window();
                String maxTime = DateFormatUtils.format(window1.maxTimestamp(), "yyyy-MM-dd HH:mm:ss.SSS");
                long size = iterable.spliterator().estimateSize();

                collector.collect("key=" + s + "的窗口[" + maxTime + ")包含" + size + "条数据===>" + iterable.toString());
            }
        });

        process.print("窗口结果：");

        env.execute();

    }
}
