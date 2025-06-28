package com.jobs.sink;

import com.jobs.bean.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class JDBCSinkDemo {
    public static void main(String[] args) throws Exception {
        //StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(4);

        Random random = new Random();

        DataGeneratorSource<User> dataGeneratorSource = new DataGeneratorSource<User>(new GeneratorFunction<Long, User>() {
            @Override
            public User map(Long aLong) throws Exception {
                return new User("name-" + aLong, random.nextInt(2), Integer.valueOf(String.valueOf(aLong)));
            }
        },
                10000/*Integer.MAX_VALUE*/,
                RateLimiterStrategy.perSecond(1000),
                Types.POJO(User.class)
        );


        DataStreamSource<User> datagen = env.fromSource(dataGeneratorSource, WatermarkStrategy.noWatermarks(), "datagen", Types.POJO(User.class));

        SinkFunction<User> sink = JdbcSink.<User>sink(
                "insert into test_user(name, sex, age) value(?, ?, ?)",
                new JdbcStatementBuilder<User>() {
                    @Override
                    public void accept(PreparedStatement preparedStatement, User user) throws SQLException {
                        preparedStatement.setString(1, user.getName());
                        preparedStatement.setInt(2, user.getSex());
                        preparedStatement.setInt(3, user.getAge());
                    }
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchIntervalMs(10000)
                        .withMaxRetries(3)
                        .withBatchSize(2000)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl("jdbc:mysql://localhost:3306/test2?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8")
                        .withUsername("root")
                        .withPassword("root")
                        .withConnectionCheckTimeoutSeconds(30)
                        .build()

        );


        datagen.addSink(sink);

        env.execute();
    }
}
