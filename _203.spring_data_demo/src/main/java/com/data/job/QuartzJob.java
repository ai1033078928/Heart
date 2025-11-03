package com.data.job;

import com.data.config.CustomProperties;
import com.data.entity.ImpDataJobEntity;
import com.data.job.task.ReadGZFilesTask;
import com.data.sql.ConfSQL;
import com.data.utils.DBUtil;
import com.data.utils.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Slf4j
@Component
public class QuartzJob extends QuartzJobBean {

    private static boolean isRunning = false;
    String curDate;

    @Autowired
    DBUtil dbUtil;
    @Autowired
    FTPUtil ftpUtil;
    @Autowired
    CustomProperties customProperties;
    @Autowired
    @Qualifier("insertTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        curDate = new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()));   // 保存执行日期

        String curTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        log.info(String.format("[%s] 任务开始执行...\n", curTime));

        if (isRunning) return;
        isRunning = true;

        // JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String jobName = customProperties.getJobName();

        List<ImpDataJobEntity> dbConfig = getDBConfig(jobName);

        log.info(String.format("任务数：%s", dbConfig.size()));
        List<ReadGZFilesTask> readDataFileTasks = getReadDataFileTasks(dbConfig);
        CountDownLatch countDownLatch = new CountDownLatch(readDataFileTasks.size());

        readDataFileTasks.forEach(task -> {
            task.setCountDownLatch(countDownLatch);
            executor.submit(task);
        });

        try {
            countDownLatch.await();
            isRunning = false;
        } catch (InterruptedException e) {
            isRunning = false;
            e.printStackTrace();
            // throw new RuntimeException(e);
        }

    }


    private List<ReadGZFilesTask> getReadDataFileTasks(List<ImpDataJobEntity> dbConfig) {
        List<ReadGZFilesTask> tasks = new ArrayList<>();
        Map<String, List<String>> pathFilesMap = new HashMap<>();

        // 初始化ftp连接信息
        log.info(String.format("Ftp配置：%s", customProperties.getConfig().toString()));
        ftpUtil.init(customProperties.getFtpHost(),
                Integer.parseInt(customProperties.getFtpPort()),
                customProperties.getFtpUsername(),
                customProperties.getFtpPasswd());
        try {
            // ftp 连接
            ftpUtil.connect();

            for (ImpDataJobEntity confRow : dbConfig) {

                // Map暂存path文件列表，减少读取次数
                if (!pathFilesMap.containsKey(confRow.getFtpPath())) {
                    // TODO：判断ftp路径是否存在
                    List<String> fileNames = ftpUtil.listFileNames(confRow.getFtpPath() + "/" + curDate);
                    log.info(String.format("路径：%s  所有文件列表：%s", confRow.getFtpPath() + "/" + curDate, fileNames));
                    // 去掉重复读取的文件，即已经处理过的文件
                    HashSet<String> curDateProcessdFiles = getCurDateProcessdFiles(confRow.getFtpPath(), curDate);
                    List<String> needFileNames = fileNames.stream()
                            .filter(s -> !curDateProcessdFiles.contains(s))
                            .collect(Collectors.toList());
                    pathFilesMap.put(confRow.getFtpPath(), needFileNames);
                    log.info(String.format("未读取文件列表：%s", needFileNames));
                }


                // 通过FTP获取文件列表；对应表没有文件要处理，跳过
                List<String> list = pathFilesMap.get(confRow.getFtpPath())
                        .stream()
                        .filter(s -> s.contains(confRow.getTableName() + "."))   // 考虑 test.a 和 test.abc 的区别
                        .collect(Collectors.toList());
                if (list.isEmpty()) continue;
                log.info(String.format("%s表待处理文件列表：%s",confRow.getTableName() ,list));
                // 若存在需要处理的文件，装配任务
                ReadGZFilesTask readGZFilesTask = new ReadGZFilesTask();
                readGZFilesTask.setImpDataJobEntity(confRow);
                readGZFilesTask.setTableColsArr(Arrays.stream(confRow.getTableCols().split(",")).map(String::trim).toArray(String[]::new));
                readGZFilesTask.setNeedColsArr(Arrays.stream(confRow.getNeedCols().split(",")).map(String::trim).toArray(String[]::new));
                readGZFilesTask.setTableColsTypeMap(confRow.getTableColsType());
                readGZFilesTask.setCurFtpPath(confRow.getFtpPath() + "/" + curDate);
                readGZFilesTask.setFiles(list);
                readGZFilesTask.setCurDate(curDate);

                tasks.add(readGZFilesTask);
            }

            return tasks;
        } catch (IOException e) {
            log.error("ftp获取目录连接失败");
            // throw new RuntimeException(e);
        } finally {
            // 关闭FTP客户端
            ftpUtil.disconnect();
        }

        return tasks;
    }

    private HashSet<String> getCurDateProcessdFiles(String ftpPath, String curDate) {
        // TODO：需要处理跨天情况？？？
        HashSet<String> hashSet = new HashSet<>();
        List<Map<String, Object>> maps = dbUtil.queryData(ConfSQL.GET_PROCESSD_FILES, ftpPath, curDate, ftpPath, curDate);
        for (Map<String, Object> map : maps) {
            hashSet.add(map.get("fileName").toString());
        }
        return hashSet;
    }

    private List<ImpDataJobEntity> getDBConfig(String jobGroup) {
        return dbUtil.query(
                ConfSQL.GET_TAB_CONF_SQL,
                (rs, rowNum) -> {
                    ImpDataJobEntity jobInfo = new ImpDataJobEntity();
                    jobInfo.setTableName(rs.getString("tableName"));
                    jobInfo.setFilePath(rs.getString("filePath"));
                    jobInfo.setFtpPath(rs.getString("ftpPath"));
                    jobInfo.setTableCols(rs.getString("tableCols"));
                    jobInfo.setNeedCols(rs.getString("needCols"));
                    jobInfo.setTableColsType(rs.getString("tableColsType"));
                    jobInfo.setTablePK(rs.getString("tablePK"));
                    jobInfo.setTableNotNullCols(rs.getString("tableNotNullCols"));
                    jobInfo.setJobGroup(rs.getString("jobGroup"));
                    jobInfo.setProcessStatus(rs.getString("processStatus"));
                    jobInfo.setIsEnabled(rs.getInt("isEnabled"));
                    return jobInfo;
                },
                jobGroup
        );
    }


}
