package example.quartz;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class MySchedulerJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();

        String jobName = jobDataMap.getString("jobName");
        String jobInfo = jobDataMap.getString("jobInfo");

        // TODO 在此创建多线程任务？？？

        System.out.println(
                StringUtils.join(Arrays.asList("curTime:", curTime, "jobName:", jobName, "jobInfo:", jobInfo, "toString:", this.toString()), " ")
        );

    }
}
