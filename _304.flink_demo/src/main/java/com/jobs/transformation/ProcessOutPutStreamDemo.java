package com.jobs.transformation;

import com.jobs.bean.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Random;

public class ProcessOutPutStreamDemo {

    public static void main(String[] args) throws Exception {
        /**
         * 侧输出流
         * process + OutputTag
         */
        // StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(2);

        Random random = new Random();

        DataGeneratorSource<User> dataGeneratorSource = new DataGeneratorSource<User>(new GeneratorFunction<Long, User>() {
            @Override
            public User map(Long aLong) throws Exception {
                return new User("name-" + aLong, random.nextInt(2), Integer.valueOf(String.valueOf(aLong)));
            }
        },
                200,
                RateLimiterStrategy.perSecond(10),
                Types.POJO(User.class)   // 必须指定为POJO，否则后续sum、max不支持该类型
        );


        OutputTag<User> ageErr = new OutputTag<>("age_err", Types.POJO(User.class));
        OutputTag<User> sex_1 = new OutputTag<>("sex_1", Types.POJO(User.class));

        SingleOutputStreamOperator<User> processed = env.fromSource(dataGeneratorSource, WatermarkStrategy.noWatermarks(), "ProcessOutPutStreamDemo")
                .process(new ProcessFunction<User, User>() {
                             @Override
                             public void processElement(User user, ProcessFunction<User, User>.Context context, Collector<User> collector) throws Exception {
                                 if (user.age > 128) {
                                     context.output(ageErr, user);
                                 } else if (user.sex == 0) {
                                     collector.collect(user);
                                 } else {
                                     context.output(sex_1, user);
                                 }
                             }
                         },
                        Types.POJO(User.class)
                );

        processed.print("主流：");

        processed.getSideOutput(ageErr).printToErr("年龄错误：");
        processed.getSideOutput(sex_1).print("性别为1：");



        env.execute();
    }
}
