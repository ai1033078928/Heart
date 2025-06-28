package com.watermark;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;

public class MyWaterMarkGenerator<T> implements WatermarkGenerator<T> {

    // 乱序等待时间
    private Long delayTs;
    // 当前为止，最大的事件事件
    private Long maxTs;

    public MyWaterMarkGenerator(Long delayTs) {
        this.delayTs = delayTs;
        this.maxTs = Long.MIN_VALUE + this.delayTs + 1;
    }

    /**
     * 每条数据来都会调用一次：用来提取最大的事件事件保存下来
     * @param event
     * @param eventTimestamp  提取到数据的事件时间
     * @param output
     */
    @Override
    public void onEvent(T event, long eventTimestamp, WatermarkOutput output) {
        System.out.println("调用onEvent方法，当前最大事件时间：" + eventTimestamp);
        maxTs = Math.max(maxTs, eventTimestamp);
        // 断点式水位线生成器  将output.emitWatermark放到此处，onPeriodicEmit方法什么都不做
    }

    /**
     * 周期性调用：发射wateramrk
     * @param output
     */
    @Override
    public void onPeriodicEmit(WatermarkOutput output) {
        // System.out.println("调用onPeriodicEmit方法，当前watermark：" + (maxTs - delayTs - 1));
        output.emitWatermark(new Watermark(maxTs - delayTs - 1));
    }
}
