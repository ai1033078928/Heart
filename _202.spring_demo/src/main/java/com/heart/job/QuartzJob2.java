package com.heart.job;

import com.heart.contants.ConstantsSQL;
import com.heart.datasource.DataSourceContextHolder;
import com.heart.datasource.DataSourceType;
import com.heart.job.pushtask.DataUploadTask;
import com.heart.service.HeartJobInfoService;
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
    JdbcTemplate jdbcTemplate;


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
            latch.countDown();
        }

    }

    private List<DataUploadTask> getDataPushTasks(JobDataMap jobDataMap) {
        String timeInterval = jobDataMap.getString("timeInterval");
        List<DataUploadTask> res = new ArrayList<>();

        // 使用 Map 作为参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("timeInterval", timeInterval);
        // 使用主数据源
        DataSourceContextHolder.setDataSourceType(DataSourceType.PRIMARY);
        List<Map<String, Object>> queried = jdbcTemplate.queryForList(ConstantsSQL.GET_TABLES_SQL, paramMap);
        for (Map<String, Object> map : queried) {
            DataUploadTask task = new DataUploadTask();
            task.setOdsTable(MapUtils.getString(map, "ods_table", ""));
            task.setTimeInterval(MapUtils.getInteger(map, "time_interval"));
            task.setTimeUnit(MapUtils.getString(map, "time_unit", ""));
            task.setNeedCols(MapUtils.getString(map, "need_cols", ""));
            task.setFromSystem(MapUtils.getString(map, "from_system", ""));
            task.setPkCols(MapUtils.getString(map, "pk_cols", ""));
            task.setStartLoadTime(MapUtils.getString(map, "start_load_time", ""));
            task.setLastLoadTime(MapUtils.getString(map, "last_load_time", ""));  // 无须在time_unit之后
            task.setDelayTime(MapUtils.getString(map, "delay_time", ""));
            task.setSendSystems(MapUtils.getString(map, "send_systems", ""));
            task.setModelSystem(MapUtils.getString(map, "model_table", ""));
            task.setModelSystem(MapUtils.getString(map, "model_system", ""));
            task.setLoadType(MapUtils.getInteger(map, "load_type"));
            task.setOtherVariable();  // 根据已知变量初始化其他内置变量，或转换已知变量

            if (task.needLoad(true)) {  // 时间判断
                res.add(task);
            }
        }
        return res;
    }

}