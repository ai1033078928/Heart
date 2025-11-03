package com.heart.job.pushtask;


import lombok.Data;

import java.util.Map;

@Data
public class UploadTask {

    protected Integer id;  // id

    // 任务属性
    protected String timeInterval;  // 推送的间隔时间
    protected String timeUnit;  // 推送的间隔时间单位day,hour,min
    protected String delayTime;  // 延迟时间
    protected String startLoadTime;  // 开始时间
    protected String lastLoadTime;  // 结束时间
    protected String thisLoadTime;  // 任务加载时间

    // 表格属性
    protected String fromSystem;  // 推送的表名的来源
    protected String sendSystem;  // 推送的目标系统名
    protected String modelSystem;  //
    protected String modelTable;  //
    protected String odsTables;  // 推送的表名或视图名
    protected String needCols;  // 需要推送的字段名
    protected String pkCols;  // 推送字段中的主键
    protected Map<String, String> srcTableMapping;   // mapping_u中表信息

    // 数据推送独有
    protected String loadType;    // 推送类型。0增量，1全量
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


}
