package example.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Main {

    public static void main(String[] args) {
        try {
            new Main().schedulerTest();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void schedulerTest() throws SchedulerException {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "心跳");
        jobDataMap.put("jobInfo", "心跳信号");

        // 创建JobBuilder 获取要执行的job
        JobDetail jobDetail = JobBuilder
                .newJob(MySchedulerJob.class)
                .withIdentity("任务名称", "任务组")
                .withDescription("任务描述信息")
                .usingJobData(jobDataMap)
                .build();

        // 创建任务触发器
        // CalendarlntervalScheduleBuilder、DailyTimelntervalScheduleBuilder、SimpleScheduleBuilder、CronScheduleBuilder
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("触发器名称", "触发器组")
                .withDescription("触发器描述信息")
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInSeconds(10)
                                .repeatForever()   // 指定触发器将无限重复
                )
                .build();

        // 创建scheduler
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        // 将jobDetail 和 trigger 进行绑定
        scheduler.scheduleJob(jobDetail, trigger);
        // 开启任务
        scheduler.start();

        // 停止进程时关闭资源
        Runtime.getRuntime().addShutdownHook(new Thread("shutdown") {
            @Override
            public void run() {
                System.out.println("close resource...... 关闭资源......");
            }
        });

        // 避免主线程退出导致 JVM 结束
        while (true) {}
    }
}