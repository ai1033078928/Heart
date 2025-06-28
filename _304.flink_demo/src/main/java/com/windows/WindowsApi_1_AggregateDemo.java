package com.windows;

import com.jobs.bean.User;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.time.Duration;

public class WindowsApi_1_AggregateDemo {

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
        // WindowedStream<User, String, TimeWindow> window = keyed.window(TumblingProcessingTimeWindows.of(Duration.ofMillis(10 * 1000)));
        WindowedStream<User, String, TimeWindow> window = keyed.window(TumblingProcessingTimeWindows.of(Duration.ofSeconds(3)));

        // 2. 窗口函数

        SingleOutputStreamOperator<String> aggregate = window.aggregate(
                /**
                 * 第一个参数：输入数据的类型
                 * 第二个参数：累加器类型，存储中间结果
                 * 第三个参数：输出的类型
                 */
                new AggregateFunction<User, Integer, String>() {

                    @Override
                    public Integer createAccumulator() {
                        System.out.println("创建累加器");
                        return 0;
                    }

                    @Override
                    public Integer add(User user, Integer integer) {
                        System.out.println("聚合逻辑：" + user.toString() + "Accumulator：" + integer);
                        return integer + user.getAge();
                    }

                    @Override
                    public String getResult(Integer integer) {
                        System.out.println("获取最终结果，窗口触发时输出");
                        return String.valueOf(integer);
                    }

                    @Override
                    public Integer merge(Integer integer, Integer acc1) {
                        System.out.println("会话窗口才会用的merge");
                        return null;
                    }
                }
        );

        aggregate.print("窗口结果：");

        env.execute();

    }
}
