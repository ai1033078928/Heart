package example.quartz.no_cross;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MySchedulerJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(MySchedulerJob.class);
    private static boolean isRunning = false;

    /**
     * 创建任务，根据静态变量及CountDownLatch判断之前的任务是否完成
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        logger.info(StringUtils.join(curTime, " 开始执行job..."));

        if (!isRunning) {
            isRunning = true;
            JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();

            String jobName = jobDataMap.getString("jobName");
            String jobInfo = jobDataMap.getString("jobInfo");

            // TODO 在此创建多线程任务？？？
            int taskNum = 5;
            CountDownLatch latch = new CountDownLatch(taskNum);
            ExecutorService executor = Executors.newFixedThreadPool(3);
            Random random = new Random();

            logger.info("开始多线程执行任务...");
            for (int i = 1; i <= taskNum; i++) {
                final int finalI = i;
                executor.submit(() -> {

                    Thread.currentThread().setName(jobName + "_" + finalI);
                    try {
                        Thread.sleep((random.nextInt(4) + 1) * 1000);  // 随机睡眠2~4秒
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    latch.countDown();

                    logger.info(StringUtils.join(Thread.currentThread().getName(), "执行完成。info：", jobInfo));
                });
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                isRunning = false;
            }

        } else {
            logger.info("上个任务未执行完，跳过本次执行");
        }

    }
}
