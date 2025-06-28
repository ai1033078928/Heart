package com.jobs.source;


import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class WCFileDemo {
    public static void main(String[] args) throws Exception {
        /**
         * 1. 添加flink-connector-files依赖
         * 2. 创建flink环境
         * 3. 读取文件（创建文件Source）
         * 4. 算子处理
         * 5. 执行
         */
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(2);
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);

        FileSource<String> fileSource = FileSource.forRecordStreamFormat(new TextLineInputFormat(), new Path("D:\\Program Files\\JetBrains\\project\\heart\\_304.flink_demo\\files\\test.txt")).build();

        DataStreamSource<String> file = env.fromSource(fileSource, WatermarkStrategy.noWatermarks(), "file");
        SingleOutputStreamOperator<Tuple2> sum = file.flatMap(new FlatMapFunction<String, Tuple2>() {
            @Override
            public void flatMap(String s, Collector<Tuple2> collector) throws Exception {
                String[] split = s.split(" ");
                for (String s1 : split) {
                    collector.collect(Tuple2.of(s1, 1));
                }
            }
        })
        .returns(Types.TUPLE(Types.STRING, Types.INT))
        .keyBy(x -> x.f0)
        .sum(1);

        sum.print();


        env.execute();
    }
}
