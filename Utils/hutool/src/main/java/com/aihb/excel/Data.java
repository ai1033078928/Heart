package com.aihb.excel;

import java.util.Date;

/**
 * 基础数据类.这里的排序和excel里面的排序一致
 */
public class Data {
    private String id;
    private String mof_div_code;
    private Date date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMof_div_code() {
        return mof_div_code;
    }

    public void setMof_div_code(String mof_div_code) {
        this.mof_div_code = mof_div_code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
