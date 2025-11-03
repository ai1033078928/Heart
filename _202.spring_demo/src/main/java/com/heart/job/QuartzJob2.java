package com.heart.job;

import com.heart.job.pushtask.DataUploadTask;
import com.heart.service.HeartJobInfoService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class QuartzJob2 implements Job {

    private static boolean isRunning = false;
    private static CountDownLatch countDownLatch;
    @Autowired
    HeartJobInfoService heartJobInfoService;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        log.info(String.format("[%s] 任务开始执行...%n", curTime));

        if (isRunning) return;
        isRunning = true;

        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String jobName = jobDataMap.getString("jobName");

        countDownLatch = new CountDownLatch(1);
        DataUploadTask dataUploadTask = new DataUploadTask();
        dataUploadTask.setCountDownLatch(countDownLatch);
        taskExecutor.execute(dataUploadTask);

        try {
            countDownLatch.await();
            isRunning = false;
        } catch (InterruptedException e) {
            isRunning = false;
            e.printStackTrace();
            // throw new RuntimeException(e);
        }

    }

}
