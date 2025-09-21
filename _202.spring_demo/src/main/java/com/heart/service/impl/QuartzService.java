package com.heart.service.impl;


import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuartzService {

    @Autowired
    private Scheduler scheduler;

    /**
     * 添加任务
     */
    public void addJob(String jobName, String jobGroup, Class<? extends Job> jobClass, String cron) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName, jobGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 删除任务
     */
    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
    }

    /**
     * 暂停任务
     */
    public void pauseJob(String jobName, String jobGroup) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jobName, jobGroup));
    }

    /**
     * 恢复任务
     */
    public void resumeJob(String jobName, String jobGroup) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jobName, jobGroup));
    }

}
