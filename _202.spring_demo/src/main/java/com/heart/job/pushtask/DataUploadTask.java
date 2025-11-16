package com.heart.job.pushtask;

import com.heart.job.pushtask.UploadTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class DataUploadTask extends UploadTask implements Runnable {
    private CountDownLatch countDownLatch;

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        log.info("balabalabala...");

        try {
            Thread.sleep(10 * 1000L);
            log.info("balabalabala...end");
        } catch (InterruptedException e) {

            // throw new RuntimeException(e);
        } finally {
            countDownLatch.countDown();
        }
    }
}
