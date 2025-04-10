package com.aihb;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;

import java.sql.SQLException;
import java.util.List;

public class OracleConnectionExample {

    public static void main(String[] args) throws SQLException {
        //查询
        List<Entity> result = Db.use().query("SELECT * FROM user_tab_columns WHERE TABLE_NAME = ? ORDER BY TABLE_NAME, COLUMN_ID", "DEBT_T_ZWGL_ZWXX");


        for (Entity entity : result) {
            System.out.println(StrUtil.format("表名：{}\t列名：{}", entity.get("TABLE_NAME"), entity.get("COLUMN_NAME")));
        }


    }
}
