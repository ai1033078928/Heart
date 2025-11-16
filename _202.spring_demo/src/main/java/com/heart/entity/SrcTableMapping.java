package com.heart.entity;

import lombok.Data;

@Data
public class SrcTableMapping {
    private String srcTableName;
    private String srcSystem;
    private String colsSeq;
    private String pkCols;
    private String colsType;
    private String notNullCols;
    private String sensitiveCols;
    private String makeDate;
    private String modifyDate;
    private String nonPkFlag;
    private String oggRoad;
}
