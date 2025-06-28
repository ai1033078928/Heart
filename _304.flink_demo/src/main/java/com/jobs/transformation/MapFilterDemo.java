package com.jobs.transformation;

import com.jobs.bean.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Random;

public class MapFilterDemo {
    public static void main(String[] args) throws Exception {
        /**
         * flatMap、Map、Fliter及富含数学习
         */

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        Random random = new Random();

        DataGeneratorSource<User> dataGeneratorSource = new DataGeneratorSource<User>(new GeneratorFunction<Long, User>() {
            @Override
            public User map(Long aLong) throws Exception {
                return new User("name-" + aLong, random.nextInt(2), Integer.valueOf(String.valueOf(aLong)));
            }
        },
                Integer.MAX_VALUE,
                RateLimiterStrategy.perSecond(2),
                Types.GENERIC(User.class)
        );

        env.fromSource(dataGeneratorSource, WatermarkStrategy.noWatermarks(), "dataGeneratorSource", Types.GENERIC(User.class))
                .map(new RichMapFunction<User, User>() {
                    @Override
                    public User map(User value) throws Exception {
                        if (value.age > 128) {
                            value.age = 128;
                        }
                        return value;
                    }

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        System.out.println(getRuntimeContext().getJobInfo() + " open");
                        super.open(parameters);
                    }

                    @Override
                    public void close() throws Exception {
                        System.out.println(getRuntimeContext().getJobInfo() + " close");
                        super.close();
                    }
                })
                .filter(new RichFilterFunction<User>() {
                    @Override
                    public boolean filter(User value) throws Exception {
                        return value.sex == 0;
                    }
                })
                .flatMap(new RichFlatMapFunction<User, User>() {
                    @Override
                    public void flatMap(User value, Collector<User> out) throws Exception {
                        out.collect(value);
                        if (value.age%2 == 0) {
                            value.age += 1;
                            value.name = value.getName() + "_new";
                            out.collect(value);
                        }
                    }
                })
                .print();


        env.execute();
    }
}
