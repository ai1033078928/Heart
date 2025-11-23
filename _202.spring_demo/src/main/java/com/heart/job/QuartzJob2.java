package com.heart.job;

import com.heart.entity.OdsLoadingJobTest;
import com.heart.job.pushtask.DataUploadTask;
import com.heart.service.IOdsLoadingJobTestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Slf4j
public class QuartzJob2 implements Job {

    private static CountDownLatch latch;

    @Autowired
    @Qualifier("taskExecutor")
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    IOdsLoadingJobTestService iOdsLoadingJobTestService;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

        if (latch != null && latch.getCount() != 0) {
            log.info("任务正在执行中...");
            return;
        }

        latch = new CountDownLatch(1);
        log.info(String.format("[%s] 任务开始执行...", curTime));
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        // DataSourceContextHolder.setDataSourceType(DataSourceType.PRIMARY);
        // List<Map<String, Object>> queried = jdbcTemplate.queryForList();

        List<DataUploadTask> tasks = getDataPushTasks(jobDataMap);
        CountDownLatch countDownLatch = new CountDownLatch(tasks.size());
        log.info("加载任务数：" + tasks.size());
        tasks.forEach(dataUploadTask -> {
            dataUploadTask.setCountDownLatch(countDownLatch);
            taskExecutor.execute(dataUploadTask);
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("任务执行异常：{}", e.getMessage());
            e.printStackTrace();
            // throw new RuntimeException(e);
        } finally {
            latch.countDown();  // 修改标识位
        }

    }

    private List<DataUploadTask> getDataPushTasks(JobDataMap jobDataMap) {
        String timeInterval = jobDataMap.getString("timeInterval");
        List<DataUploadTask> res = new ArrayList<>();

        List<Integer> timeIntervalList = Arrays.stream(timeInterval.split(",")).map(Integer::getInteger).collect(Collectors.toList());
        List<OdsLoadingJobTest> activeLoadingJobs = iOdsLoadingJobTestService.getActiveLoadingJobs(timeIntervalList);
        for (OdsLoadingJobTest job : activeLoadingJobs) {
            DataUploadTask task = new DataUploadTask();
            task.setOdsTable(job.getOdsTable());
            task.setTimeInterval(job.getTimeInterval());
            task.setTimeUnit(job.getTimeUnit());
            task.setNeedCols(job.getNeedCols());
            task.setFromSystem(job.getFromSystem());
            task.setPkCols(job.getPkCols());
            task.setStartLoadTime(job.getStartLoadTime());
            task.setLastLoadTime(job.getLastLoadTime());  // 无须在time_unit之后
            task.setDelayTime(job.getDelayTime());
            task.setSendSystems(job.getSendSystems());
            task.setModelTable(job.getModelTable());
            task.setModelSystem(job.getModelSystem());
            task.setLoadType(job.getLoadType());
            task.setOtherVariable();  // 根据已知变量初始化其他内置变量，或转换已知变量

            if (task.needLoad(true)) {  // 时间判断
                res.add(task);
            }
        }
        return res;
    }

}