package com.data.entity;

import lombok.Data;

@Data
public class ImpDataDdlEntity {
    private Long id;
    private String tableName;
    private String fileName;
    private String filePath;
    private String ftpPath;
    private String ddlFlag;
    private String ddlContent;
    private String operator;
    private String operateTime;
    private String remarks;
}
