package com.jobs.transformation;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 连接两条流，实现inner join效果（实际不会使用：内存问题、分布式问题、线程问题）
 */
public class ConnectKeyByDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataStreamSource<Tuple2<Integer, String>> elements1 = env.fromElements(
                Tuple2.of(1, "a1"),
                Tuple2.of(1, "a2"),
                Tuple2.of(2, "b"),
                Tuple2.of(3, "c")
        );
        DataStreamSource<Tuple3<Integer, String, Integer>> elements2 = env.fromElements(
                Tuple3.of(1, "aa1", 1),
                Tuple3.of(1, "aa2", 2),
                Tuple3.of(2, "bb", 1),
                Tuple3.of(3, "cc", 1)
        );

        ConnectedStreams<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>> connected = elements1.connect(elements2);

        /**
         * 实现互相匹配的效果
         * 1. 两条流，不一定哪个先来数据
         * 2. 每条流有数据来的时候，除了存变量中，不知道另一流是否有匹配的数据，要去另一条流的变量中，查找是否有能匹配上的
         */
        SingleOutputStreamOperator<String> process = connected
                .keyBy(s1 -> s1.f0, s2 -> s2.f0)   // 多并行度下，需要根据关联条件keyby，才能匹配上
                .process(new CoProcessFunction<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>, String>() {
                    HashMap<Integer, List<Tuple2<Integer, String>>> hashMap1 = new HashMap<>();
                    HashMap<Integer, List<Tuple3<Integer, String, Integer>>> hashMap2 = new HashMap<>();

                    /**
                     * 第一提条流的处理逻辑
                     * @param integerStringTuple2  第一条流的数据
                     * @param context  上下文
                     * @param collector  采集器
                     * @throws Exception
                     */
                    @Override
                    public void processElement1(Tuple2<Integer, String> integerStringTuple2, CoProcessFunction<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>, String>.Context context, Collector<String> collector) throws Exception {
                        Integer id = integerStringTuple2.f0;
                        // 数据来了缓存到变量中
                        if (!hashMap1.containsKey(id)) {
                            ArrayList<Tuple2<Integer, String>> list = new ArrayList<>();
                            list.add(integerStringTuple2);
                            hashMap1.put(id, list);
                        } else {
                            hashMap1.get(id).add(integerStringTuple2);
                        }

                        // 另一个流中匹配
                        if (hashMap2.containsKey(id)) {
                            for (Tuple3<Integer, String, Integer> s2Element : hashMap2.get(id)) {
                                collector.collect("s1" + integerStringTuple2 + " ======= s2" + s2Element);
                            }
                        }
                    }

                    @Override
                    public void processElement2(Tuple3<Integer, String, Integer> integerStringIntegerTuple3, CoProcessFunction<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>, String>.Context context, Collector<String> collector) throws Exception {
                        Integer id = integerStringIntegerTuple3.f0;
                        // 数据来了缓存到变量中
                        if (!hashMap2.containsKey(id)) {
                            ArrayList<Tuple3<Integer, String, Integer>> list = new ArrayList<>();
                            list.add(integerStringIntegerTuple3);
                            hashMap2.put(id, list);
                        } else {
                            hashMap2.get(id).add(integerStringIntegerTuple3);
                        }

                        // 另一个流中匹配
                        if (hashMap1.containsKey(id)) {
                            for (Tuple2<Integer, String> s1Element : hashMap1.get(id)) {
                                collector.collect("s1" + s1Element + " ======= s2" + integerStringIntegerTuple3);
                            }
                        }
                    }
                });

        process.print();


        env.execute();
    }
}
