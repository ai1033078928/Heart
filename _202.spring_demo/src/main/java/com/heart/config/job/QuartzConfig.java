package com.heart.config.job;

import com.heart.job.QuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
public class QuartzConfig {

    @Autowired
    CustomProperties customProperties;

    @Bean
    public JobDetail myJobDetail() {
        Map<String, String> mapProperties = customProperties.getMapProperties();
        JobDataMap jobDataMap = new JobDataMap(mapProperties);
        log.info("加载文件配置：{}", jobDataMap);

        log.info("开始定义任务...");
        // 定义任务
        return JobBuilder.newJob(QuartzJob.class)
                .withDescription("任务描述")
                .withIdentity("任务", "任务组1")
                .storeDurably()
                .usingJobData(jobDataMap)
                .build();
    }

    @Bean
    public Trigger myJobTrigger(JobDetail jobDetail) {
        log.info("开始定义触发器...");
        // 定义触发器
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("触发器", "任务组1")
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(10) // 每10秒执行一次
                                .repeatForever()
                )
                .build();
    }
}
