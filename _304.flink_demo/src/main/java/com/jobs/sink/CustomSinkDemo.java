package com.jobs.sink;

import com.jobs.bean.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.execution.CheckpointingMode;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.Random;

public class CustomSinkDemo {

    /**
     * 一般不用自己定义 Source、Sink  需要考虑会跟多东西，比如出错重试
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        // env.enableCheckpointing(2000, CheckpointingMode.EXACTLY_ONCE);

        FileSource<String> fileSource = FileSource.forRecordStreamFormat(new TextLineInputFormat(), new Path("D:\\Program Files\\JetBrains\\project\\heart\\_304.flink_demo\\files\\test.txt")).build();
        DataStreamSource<String> file = env.fromSource(fileSource, WatermarkStrategy.noWatermarks(), "file");

        MySink mySink = new MySink();
        file.addSink(mySink);

        env.execute();
    }


    public static class MySink extends RichSinkFunction<String> {

        /**
         * Sink 核心逻辑
         * @param value
         * @param context
         * @throws Exception
         */
        @Override
        public void invoke(String value, Context context) throws Exception {
            // 写逻辑， 来一条数据调用一次，所以不要在此创建连接
            // 调用连接的执行方法
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            // 创建连接
            super.open(parameters);
        }

        @Override
        public void close() throws Exception {
            // 销毁连接
            super.close();
        }
    }
}
