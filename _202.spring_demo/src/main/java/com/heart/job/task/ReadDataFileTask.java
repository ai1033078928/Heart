package com.heart.job.task;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ReadDataFileTask implements Callable {
    CountDownLatch countDownLatch;

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Object call() throws Exception {
        System.out.println(String.format("任务%s执行%n", countDownLatch.getCount()));
        log.info(String.format("任务%s执行%n", countDownLatch.getCount()));

        countDownLatch.countDown();
        return null;
    }
}
