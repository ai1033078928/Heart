package com.hrbu.cron;

import cn.hutool.core.lang.Console;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import org.junit.Test;

/**
 * 配置文件cron.setting
 */
public class 定时任务 {


    @Test
    public void Main() throws InterruptedException {
        // 配置文件写法不需要调用就可以执行？
        Test2();
        Thread.currentThread().sleep(30000);
    }

    /**
     * 定时任务
     */
    public void Test() {
        System.out.println("hello world!!!");
    }

    /**
     * 动态添加定时任务
     */
    public void Test2() {
        CronUtil.schedule("*/2 * * * * *", new Task() {
            @Override
            public void execute() {
                Console.log("Task excuted.");
            }
        });

        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }
}
