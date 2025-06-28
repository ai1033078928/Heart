package com.windows;

import com.jobs.bean.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.time.Duration;
import java.util.Random;

public class WindowsApi_0_Demo {

    public static void main(String[] args) throws Exception {

        //StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        Random random = new Random();
        DataGeneratorSource<User> dataGeneratorSource = new DataGeneratorSource<User>(
                (GeneratorFunction<Long, User>) aLong -> new User("name-" + aLong, random.nextInt(2), Integer.valueOf(String.valueOf(aLong))),
                Integer.MAX_VALUE,
                RateLimiterStrategy.perSecond(10),
                Types.POJO(User.class)
        );


        DataStreamSource<User> datagen = env.fromSource(dataGeneratorSource, WatermarkStrategy.noWatermarks(), "datagen", Types.POJO(User.class));

        KeyedStream<User, String> keyed = datagen.keyBy(User::getName);

        // TODO 1. 指定窗口分配器：指定哪种窗口  时间-计数   滚动、滑动、会话窗口
        // 1.1 没有keyby的窗口 : 窗口内所有数据进入一个子任务，即并行度为1
        // datagen.windowAll()

        // 1.2 keyby的窗口 : 每个key定义一个窗口，各自独立进行计算

        // 1.2.1 基于时间
        // -- 基于时间滚动窗口
        // keyed.window(TumblingProcessingTimeWindows.of(Duration.ofSeconds(10)));
        // -- 基于时间的滚动窗口，窗口长度10s，步长2s
        // keyed.window(SlidingProcessingTimeWindows.of(Duration.ofSeconds(10), Duration.ofSeconds(2)));
        // -- 会话窗口，超时间隔为5s
        // keyed.window(ProcessingTimeSessionWindows.withGap(Duration.ofSeconds(5)));


        // 1.2.2 基于计数
        // -- 滚动窗口，长度5个元素
        // keyed.countWindow(5);
        // -- 滑动窗口，窗口长度5个元素，滑动步长2个元素
        // keyed.countWindow(5, 2);
        // -- 全局窗口，计数窗口底层用的这个（需要自定义触发器、驱逐器，很少用）
        // keyed.window(GlobalWindows.create()).evictor().trigger();

        // TODO 2. 指定窗口函数：窗口内的计算逻辑
        WindowedStream<User, String, TimeWindow> window = keyed.window(TumblingProcessingTimeWindows.of(Duration.ofSeconds(10)));
        // 增量聚合：来一条数据计算一条，窗口触发的时候输出计算结果   .reduce()/.aggregate()
        // window.reduce()
        // window.aggregate()
        // 全窗口函数：数据来了不计算存起来，窗口触发的时候，计算并输出结果   .process()/.apply()
        // window.process()
        // window.apply()

        env.execute();

    }
}
