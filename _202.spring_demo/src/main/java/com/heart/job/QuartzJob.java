package com.heart.job;

import com.heart.job.task.ReadDataFileTask;
import com.heart.service.HeartJobInfoService;
import com.heart.entity.HeartJobInfoEntity;
import com.heart.job.task.ReadGzipFilesTask;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuartzJob implements Job {

    private static boolean isRunning = false;
    private static CountDownLatch countDownLatch;
    @Autowired
    HeartJobInfoService heartJobInfoService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        System.out.printf("[%s] 任务开始执行...%n", curTime);

        if (isRunning) return;
        isRunning = true;

        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String jobName = jobDataMap.getString("jobName");

        List<String> jobNames = Arrays.asList(jobName.split(","));
        List<HeartJobInfoEntity> jobInfos = heartJobInfoService.getJobInfoByServerName(jobNames);

        System.out.println(String.format("任务数：%s", jobInfos.size()));
        countDownLatch = new CountDownLatch(jobInfos.size());
        List<ReadGzipFilesTask> readDataFileTasks = getReadDataFileTasks(jobInfos);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        readDataFileTasks.forEach(executor::submit);

        try {
            countDownLatch.await();
            isRunning = false;
        } catch (InterruptedException e) {
            isRunning = false;
            e.printStackTrace();
            // throw new RuntimeException(e);
        }

    }

    private List<ReadDataFileTask> getReadDataFileTasksTest(List<HeartJobInfoEntity> jobInfos) {
        List<ReadDataFileTask> tasks = new ArrayList<>();

        for (HeartJobInfoEntity jobInfo : jobInfos) {
            ReadDataFileTask task = new ReadDataFileTask();
            task.setCountDownLatch(countDownLatch);

            tasks.add(task);
        }

        return tasks;
    }

    private List<ReadGzipFilesTask> getReadDataFileTasks(List<HeartJobInfoEntity> jobInfos) {
        List<ReadGzipFilesTask> tasks = new ArrayList<>();

        for (HeartJobInfoEntity jobInfo : jobInfos) {
            ReadGzipFilesTask task = new ReadGzipFilesTask("", "","", "");
            task.setCountDownLatch(countDownLatch);

            tasks.add(task);
        }

        return tasks;
    }
}
