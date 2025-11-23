package com.heart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 推送下游任务加载新表
 * </p>
 *
 * @author ahb
 * @since 2025-11-18
 */
@Getter
@Setter
@ToString
@TableName("ods_loading_job_test")
public class OdsLoadingJobTest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("ods_table")
    private String odsTable;

    /**
     * 推送的时间间隔
     */
    @TableField("time_interval")
    private Integer timeInterval;

    /**
     * 推送的时间间隔的单位day,hour,min
     */
    @TableField("time_unit")
    private String timeUnit;

    /**
     * 延迟时间
     */
    @TableField("delay_time")
    private String delayTime;

    @TableField("model_table")
    private String modelTable;

    /**
     * 与etl.src_table_mapping相对应
     */
    @TableField("model_system")
    private String modelSystem;

    /**
     * 需要推送的字段名
     */
    @TableField("need_cols")
    private String needCols;

    /**
     * 推送字段中的主键
     */
    @TableField("pk_cols")
    private String pkCols;

    /**
     * 上一批次中最大（下一批次中最小）的kuduloadtime
     */
    @TableField("start_load_time")
    private String startLoadTime;

    /**
     * 加载完成的时间;重推数据时为数据截至时间（最终文件名称）
     */
    @TableField("last_load_time")
    private String lastLoadTime;

    /**
     * 推送的表名的来源
     */
    @TableField("from_system")
    private String fromSystem;

    /**
     * 推送的目标系统名
     */
    @TableField("send_systems")
    private String sendSystems;

    /**
     * 推送的状态
     */
    @TableField("load_status")
    private String loadStatus;

    /**
     * 1开始推送，0停止推送;重推数据需保证正常推送停止，即字段为0
     */
    @TableField("load_enable")
    private String loadEnable;

    /**
     * 推送类型。0增量，1全量
     */
    @TableField("load_type")
    private Integer loadType;

    /**
     * 容忍延迟推送时间
     */
    @TableField("tolerate_delays")
    private Integer tolerateDelays;

    /**
     * 是否有ddl变更。0无，1有
     */
    @TableField("ddl_type")
    private Integer ddlType;

    /**
     * 创建时间
     */
    @TableField("make_date")
    private LocalDateTime makeDate;
}
