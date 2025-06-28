package com.jobs.sink;

import com.jobs.bean.User;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.core.execution.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Random;


/**
 * 实现Kafka精准一次
 * 1. 必须设置CheckPoint
 * 2. 必须设置事务前缀
 * 3. 必须设置事务超时时间   checkpoint间隔 < 超时时间 < max的15min
 *
 * 自定义序列化器
 * 1. 实现接口，重写序列化方法
 * 2. 指定key转为字节数组
 * 3. 指定value转为字节数组
 * 4. 返回ProducerRecord对象
 */
public class KafkaSinkWithKeyDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        // kafka实现精准一次  必须开启Checkpoint，否则在精准一次无法写入kafka
        env.enableCheckpointing(2000, CheckpointingMode.EXACTLY_ONCE);

        Random random = new Random();
        DataGeneratorSource<String> dataGeneratorSource = new DataGeneratorSource<String>(
                new GeneratorFunction<Long, String>() {
                    @Override
                    public String map(Long aLong) throws Exception {
                        User user = new User("name-" + aLong, random.nextInt(2), Integer.valueOf(String.valueOf(aLong)));
                        return user.toString();
                    }
                },
                Integer.MAX_VALUE,
                RateLimiterStrategy.perSecond(10),
                Types.STRING
        );


        DataStreamSource<String> datagen = env.fromSource(dataGeneratorSource, WatermarkStrategy.noWatermarks(), "KafkaSinkSemo");

        // 输出到Kafka
        KafkaSink<String> sink = KafkaSink
                .<String>builder()
                // kafka地址端口
                .setBootstrapServers("8.8.8.10:9092")
                // 指定序列化器
                .setRecordSerializer(
                        new KafkaRecordSerializationSchema<String>() {
                            @Nullable
                            @Override
                            public ProducerRecord<byte[], byte[]> serialize(String element, KafkaSinkContext context, Long timestamp) {
                                byte[] key = String.valueOf(random.nextInt()).getBytes(StandardCharsets.UTF_8);
                                byte[] value = element.getBytes(StandardCharsets.UTF_8);
                                return new ProducerRecord<>("topic-1", key, value);
                            }
                        }
                        /*KafkaRecordSerializationSchema
                                .<String>builder()
                                .setTopic("topic-1")
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .build()*/
                )
                // 一致性级别
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                // 如果精准一次，必须设置事务前缀
                .setTransactionalIdPrefix("flink-kafka-")
                // 如果精准一次，必须设置事务超时时间  大于checkpoint，小于15min
                .setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, String.valueOf(10*60*1000))
                .build();


        datagen.sinkTo(sink);

        env.execute();
    }
}
