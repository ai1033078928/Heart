package com.jobs.sink;

import com.jobs.bean.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.sink.Sink;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.execution.CheckpointingMode;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Random;

/**
 * 1.12前  addSink
 * 1.12后  sinkTo
 */
public class FileSinkDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        // 必须开启Checkpoint，否则文件一直是 .inprogress
        env.enableCheckpointing(2000, CheckpointingMode.EXACTLY_ONCE);

        Random random = new Random();
        DataGeneratorSource<User> dataGeneratorSource = new DataGeneratorSource<User>(
                new GeneratorFunction<Long, User>() {
                    @Override
                    public User map(Long aLong) throws Exception {
                        return new User("name-" + aLong, random.nextInt(2), Integer.valueOf(String.valueOf(aLong)));
                    }
                },
                Integer.MAX_VALUE,
                RateLimiterStrategy.perSecond(10),
                Types.POJO(User.class)
        );


        DataStreamSource<User> datagen = env.fromSource(dataGeneratorSource, WatermarkStrategy.noWatermarks(), "datagen", Types.POJO(User.class));

        // 输出到文件系统
        FileSink<User> sink = FileSink
                .<User>forRowFormat(new Path("D:\\Desktop\\tmp"), new SimpleStringEncoder<>("UTF-8"))
                // 文件配置，前缀后缀
                .withOutputFileConfig(
                        OutputFileConfig
                                .builder()
                                .withPartPrefix("test-")
                                .withPartSuffix(".log")
                                .build()
                )
                // 按目录分桶 按小时分桶
                .withBucketAssigner(new DateTimeBucketAssigner<>("yyyy-MM-dd HH", ZoneId.systemDefault()))
                // 文件滚动策略
                .withRollingPolicy(
                        DefaultRollingPolicy
                                .builder()
                                .withRolloverInterval(Duration.ofSeconds(10))
                                .withMaxPartSize(new MemorySize(1024 * 1024))
                                .build()
                )
                .build();

        datagen.sinkTo(sink);

        env.execute();
    }
}
