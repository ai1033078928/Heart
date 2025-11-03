package com.data.entity;

import lombok.Data;

@Data
public class ImpDataFileEntity {
    private Long id;
    private String fileName;
    private String filePath;
    private String ftpPath;
    private Long fileSize;
    private String processStatus;
    private String startTime;
    private String endTime;
    private String errorInfo;
    private Integer recordCount;

}
