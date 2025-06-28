package com.jobs.source;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WCSocketDemo {
    public static void main(String[] args) throws Exception {

        // StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // UI界面：http://localhost:8081/#/overview
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(2);

        DataStreamSource<String> socketTextStream = env.socketTextStream("172.24.203.99", 7777);

        socketTextStream.flatMap(new FlatMapFunction<String, Tuple2>() {
                    @Override
                    public void flatMap(String s, Collector<Tuple2> collector) throws Exception {
                        String[] split = s.split(" ");
                        for (String s1 : split) {
                            collector.collect(Tuple2.of(s1, 1));
                        }
                    }
                })
                .setParallelism(3)          // 算子级别并行度
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(x -> x.f0)
                .sum(1)
                .disableChaining()          // 断开算子链
                .print();

        env.execute();
    }
}
