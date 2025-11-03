package com.heart.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// 数据实体
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@HeadRowHeight(20)
@ColumnWidth(25)
public class MyData {

    @ColumnWidth(40)
    @ExcelProperty(value = "BAL_ID", index = 0)
    private String id;

    private String code;

    // @ExcelIgnore
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String update_time;

    // 接收百分比数据
    // @NumberFormat("#.##%")
    // private String numData;

}
