package com.jobs.transformation;

import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

public class UinonConnectStreamDemo {
    public static void main(String[] args) throws Exception {
        /**
         * 1. union
         * 2. connect + CoMapFunction（keyby）
         */
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);


        DataStreamSource<Integer> data1 = env.fromData(1, 2, 3);
        DataStreamSource<Integer> data2 = env.fromData(100, 200, 300);
        DataStreamSource<String> data3 = env.fromData("111", "222", "333");

        // 合流（类型需一致）
        // DataStream<Integer> unioned = data1.union(data2, data3.map(Integer::valueOf));
        // unioned.print("unioned：");

        // 连接（类型可不一致）
        ConnectedStreams<Integer, String> connected = data1.connect(data3);
        SingleOutputStreamOperator<Integer> map = connected.map(new CoMapFunction<Integer, String, Integer>() {
            @Override
            public Integer map1(Integer integer) throws Exception {
                return integer;
            }

            @Override
            public Integer map2(String s) throws Exception {
                return Integer.valueOf(s);
            }
        });
        map.print("connected：");

        env.execute();

    }
}
