package com.heart.job.pushtask;


import com.heart.entity.SrcTableMapping;
import com.heart.utils.DateStrFormatUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public abstract class UploadTask {

    protected Integer id;  // id

    // 任务属性
    protected Integer timeInterval;  // 推送的间隔时间
    protected String timeUnit;  // 推送的间隔时间单位day,hour,min
    protected String delayTime;  // 延迟时间
    protected String startLoadTime;  // 开始时间
    protected String lastLoadTime;  // 结束时间
    protected String thisLoadTime;  // 任务加载时间

    // 表格属性
    protected String fromSystem;  // 推送的表名的来源
    protected String sendSystems;  // 推送的目标系统名
    protected String[] sendSystemsArr;  // 衍生变量
    protected String modelSystem;  //
    protected String modelTable;  //
    protected String odsTable;  // 推送的表名或视图名
    protected String needCols;  // 需要推送的字段名
    protected String pkCols;  // 推送字段中的主键
    protected SrcTableMapping srcTableMapping;   // mapping_u中表信息

    // 数据推送独有
    protected Integer loadType;    // 推送类型。0增量，1全量
    // 重推数据独有
    protected String reloadType;    // 推送的数据量I增量,F全量
    protected String retransmissionFlag;  // 0时是正常推送的累计，1时是一直推00
    protected String reloadTable;    // 重推数据来源表
    // ddl推送独有
    protected String ddlType;    // 新增字段add修改chg新增表为ini
    protected String ddlCols;    // 有变动的字段

    // 文件属性
    protected String CHARSET = "UTF-8";  // 文件编码
    protected Long CUTSIZE = 2 * 1024L * 1024L * 1024L;  // 单个文件大小上限
    protected String fileNamePart;  // 衍生变量：部分文件名

    public String getLastLoadTimeDalay() {
        // 2025-11-11 11:11:11 -> 2025-11-11 00:00:00
        return lastLoadTime.substring(0, 11).concat(delayTime);
    }

    /**
     * 根据已知变量初始化其他内置变量，或转换已知变量
     */
    public void setOtherVariable() {
        // 格式化时间字符串
        startLoadTime = DateStrFormatUtil.rightPadTo26(startLoadTime);

        // 处理加载时间
        if (null == lastLoadTime || lastLoadTime.isEmpty()) {
            // lastLoadTime 为空时，基于当前时间获取下一个调度时间点
            lastLoadTime = DateStrFormatUtil.getPointTime(1, delayTime, timeUnit);
        }
        thisLoadTime = DateStrFormatUtil.getPointTime(0, delayTime, timeUnit);

        // 初始化部分文件名
        List<String> systems = Arrays.asList(sendSystemsArr);
        String filePartTime = ("day".equals(timeUnit) && systems.contains("dmd") && systems.size() == 1)
                ? DateStrFormatUtil.getPointTime(0, delayTime, "hour")
                : thisLoadTime;
        this.fileNamePart = odsTable + "." + DateStrFormatUtil.getDateStrNoSeparator(filePartTime);  // db.tname.yyyyMMddhhmmss
    }

    /**
     * 判断是否需要加载任务
     * @return
     */
    public boolean needLoad(Boolean isDataDalay) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime lastLoadTime = LocalDateTime.parse(getLastLoadTime(), formatter);
        long lastLoadTimeTs = lastLoadTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // 截取和delayTime组合后的时间
        LocalDateTime lastLoadTimeDalay = LocalDateTime.parse(getLastLoadTimeDalay(), formatter);
        long lastLoadTimeDalayTs = lastLoadTimeDalay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long nextMonth = lastLoadTimeDalay.atZone(ZoneId.systemDefault()).plusMonths(getTimeInterval()).plusHours(1).toInstant().toEpochMilli();

        switch (getTimeUnit()) {
            case "month":
                return new Date().after(new Date(nextMonth));
            case "day":
                return System.currentTimeMillis() - lastLoadTimeDalayTs - 24L * 60 * 60 * 1000 * getTimeInterval() > (isDataDalay ? 60 * 60 * 1000L : 0L);
            case "hour":
                return System.currentTimeMillis() - lastLoadTimeTs - 60L * 60 * 1000 * getTimeInterval() > (isDataDalay ? 5 * 60 * 1000L : 0L);
            default:
                return System.currentTimeMillis() - lastLoadTimeTs - 60L * 1000 * getTimeInterval() > (isDataDalay ? 5 * 60 * 1000L : 0L);
        }

    }

}
