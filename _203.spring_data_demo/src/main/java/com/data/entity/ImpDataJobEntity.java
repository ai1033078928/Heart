package com.data.entity;

import lombok.Data;

@Data
public class ImpDataJobEntity {
    private String tableName;
    private String filePath;
    private String ftpPath;
    private String tableCols;
    private String needCols;
    private String tableColsType;
    private String tablePK;
    private String tableNotNullCols;
    private String jobGroup;
    private String processStatus;
    private Integer isEnabled;

}
