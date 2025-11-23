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
 * 推送配置表
 * </p>
 *
 * @author ahb
 * @since 2025-11-18
 */
@Getter
@Setter
@ToString
@TableName("ods_send_info_test")
public class OdsSendInfoTest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 推送目标系统名
     */
    @TableField("send_system")
    private String sendSystem;

    /**
     * 推送的表名
     */
    @TableField("ods_table")
    private String odsTable;

    /**
     * 推送的时间间隔
     */
    @TableField("time_interval")
    private Integer timeInterval;

    /**
     * 推送的时间间隔day,hour,min
     */
    @TableField("time_unit")
    private String timeUnit;

    @TableField("limit_min")
    private Integer limitMin;

    /**
     * 推送的字段名
     */
    @TableField("need_cols")
    private String needCols;

    @TableField("send_enable")
    private String sendEnable;

    /**
     * 延迟时间
     */
    @TableField("delay_time")
    private String delayTime;

    /**
     * 创建时间
     */
    @TableField("make_date")
    private LocalDateTime makeDate;
}
