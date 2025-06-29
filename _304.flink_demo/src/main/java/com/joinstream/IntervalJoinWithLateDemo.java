package com.joinstream;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.time.Duration;

public class IntervalJoinWithLateDemo {
    /**
     * 1. 间隔联结：每个数据到另一个流指定范围匹配，只支持时间事件语义
     * 2. 指定上下界，负数代表时间往前，正数代表时间往后
     * 3. process中只能处理关联上的数据
     * 4. 两条流关联后的watermark，以两条流中最小的为准
     * 5. 如果数据事件事件 < 当前watermark 就是迟到数据，Process不做处理
     */
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        SingleOutputStreamOperator<Tuple2<String, Integer>> elem1 = env
                .socketTextStream("172.24.204.131", 7777)
                .map(new MapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(String value) throws Exception {
                        String[] split = value.split(",");
                        return Tuple2.of(split[0], Integer.valueOf(split[1]));
                    }
                })
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<Tuple2<String, Integer>>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple2<String, Integer>>() {
                                    @Override
                                    public long extractTimestamp(Tuple2<String, Integer> element, long recordTimestamp) {
                                        return element.f1 * 1000L;
                                    }
                                })
                );

        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> elem2 = env
                .socketTextStream("172.24.204.131", 8888)
                .map(new MapFunction<String, Tuple3<String, Integer, Integer>>() {
                    @Override
                    public Tuple3<String, Integer, Integer> map(String value) throws Exception {
                        String[] split = value.split(",");
                        return Tuple3.of(split[0], Integer.valueOf(split[1]), Integer.valueOf(split[2]));
                    }
                })
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<Tuple3<String, Integer, Integer>>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple3<String, Integer, Integer>>() {
                                    @Override
                                    public long extractTimestamp(Tuple3<String, Integer, Integer> element, long recordTimestamp) {
                                        return element.f1 * 1000L;
                                    }
                                })
                );

        // TODO interval join
        OutputTag<Tuple2<String, Integer>> outputTagLeft = new OutputTag<>("left", Types.TUPLE(Types.STRING, Types.INT));
        OutputTag<Tuple3<String, Integer, Integer>> outputTagRight = new OutputTag<>("right", Types.TUPLE(Types.STRING, Types.INT, Types.INT));
        // 1. 按关联条件keyby
        KeyedStream<Tuple2<String, Integer>, String> keyed1 = elem1.keyBy(value -> value.f0);
        KeyedStream<Tuple3<String, Integer, Integer>, String> keyed2 = elem2.keyBy(value -> value.f0);
        // 2. 调用关联
        SingleOutputStreamOperator<String> process = keyed1
                .intervalJoin(keyed2)
                .between(Duration.ofSeconds(-3), Duration.ofSeconds(3))
                .sideOutputLeftLateData(outputTagLeft)   // TODO 将k1迟到数据放入侧流
                .sideOutputRightLateData(outputTagRight)  // TODO 将k2迟到数据放入侧流
                .process(new ProcessJoinFunction<Tuple2<String, Integer>, Tuple3<String, Integer, Integer>, String>() {
                    @Override
                    public void processElement(Tuple2<String, Integer> left, Tuple3<String, Integer, Integer> right, ProcessJoinFunction<Tuple2<String, Integer>, Tuple3<String, Integer, Integer>, String>.Context ctx, Collector<String> out) throws Exception {
                        out.collect(left + " ===== " + right);
                    }
                });

        process.print();

        process.getSideOutput(outputTagLeft).printToErr("left:");
        process.getSideOutput(outputTagRight).printToErr("right:");

        env.execute();

    }
}
