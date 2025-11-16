package com.heart.contants;

/**
 * 常量SQL字符串类
 */
public class ConstantsSQL {

    // 私有构造函数防止实例化
    private ConstantsSQL(){}

    public static final String GET_TABLES_SQL = "select " +
            " ods_table, time_interval,time_unit, need_cols, from_system, pk_cols,DATE_ADD(start_load_time,INTERVAL -5 minute) start_load_time, last_load_time,delay_time, send_systems,model_table,model_system,load_type " +
            " from ods_loading_job_test where load_status='Active' and load_enable='1'  and time_interval in (:timeInterval)  ";
}
