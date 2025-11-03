package com.data.sql;

public class ConfSQL {
    public static final String GET_TAB_CONF_SQL = "select * from etl.import_data_job_info where isEnabled = '1' and jobGroup = ?;";
    public static final String GET_PROCESSD_FILES = "select fileName from etl.import_data_file_log " +
            " where processStatus = 'Success' and ftpPath = ? and substr(endTime, 1, 10) = ? " +
            " union all " +
            " select fileName from etl.import_data_ddl_log where ftpPath = ? and substr(operatetime, 1, 10) = ? ;";
}
