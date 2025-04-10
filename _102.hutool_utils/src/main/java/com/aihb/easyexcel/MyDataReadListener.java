package com.aihb.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;

/**
 * 读取Excel的监听器，用于处理读取产生的数据
 */
public class MyDataReadListener extends AnalysisEventListener<MyData> {

    // private int num = 0;
    private final List<MyData> rows = new ArrayList<>();

    // 每读取一条数据，执行一次
    @Override
    public void invoke(MyData myData, AnalysisContext analysisContext) {
        // num ++;
        // System.out.println("正在读取" + num + "行数据" + myData);
        rows.add(myData);
    }

    // 读取完成，执行一次
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("数据读取完成");
    }

    public List<MyData> getAllData() {
        return rows;
    }
}
