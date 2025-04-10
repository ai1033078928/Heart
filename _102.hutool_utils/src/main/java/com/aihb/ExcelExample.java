package com.aihb;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ExcelExample {

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("债务基本信息表", "DEBT_T_ZWGL_ZWXX");
        map.put("债务举借信息",  "DEBT_T_ZWGL_JJXX");
        map.put("债务还本计划",  "DEBT_T_ZWGL_HKJH");
        map.put("债务还本计划（变更）",  "DEBT_T_ZWGL_HKJH_BG");
        map.put("债务偿还本金",  "DEBT_T_ZWGL_CHBJ");
        map.put("债务转化表", "DEBT_T_ZWGL_ZWZH");
        map.put("债务利息罚息",  "DEBT_T_ZWGL_LXFX");
        map.put("债务支出信息",  "DEBT_T_ZWGL_ZCXX");
        map.put("债务转贷表", "DEBT_T_ZWGL_ZWZD");
        map.put("存量债务余额（跑批）",  "DEBT_T_ZWGL_TOTALYE");
        map.put("FACT债券债务余额（跑批）",  "DEBT_T_FACT_ZQZWYE");
        map.put("政府支出事项主表",  "DEBT_T_RZPT_ZCSX_SXXX");
        map.put("政府支出事项动态变化情况表", "DEBT_T_RZPT_ZCSX_XXBG");
        map.put("政府支出事项纳入预算表", "DEBT_T_RZPT_ZCSX_NRYS");
        map.put("政府支出事项实际支出表", "DEBT_T_RZPT_ZCSX_SJZC");
        map.put("支出事项和项目关联表",  "DEBT_T_ZWGL_XMZC_RELATION");
        map.put("支出事项余额（跑批）",  "DEBT_T_ZCSX_TOTALYE");
        map.put("隐性债务totalye不考虑重大审批结果的（跑批）", "DEBT_T_YXZW_TOTALYE_V2");
        map.put("隐性债务totalye考虑重大审批结果的（跑批）",  "DEBT_T_YXZW_TOTALYE_SIMPLE");
        map.put("隐性债务tochbj不考虑重大审批结果的（跑批）",  "DEBT_T_YXZW_TOCHBJ_V2");
        map.put("债务认定结果表", "DEBT_T_RZPT_ZWRD");
        map.put("变动台账表", "DEBT_T_YXZW_BDTZ");
        map.put("化债计划主单",  "DEBT_T_ZWGL_HZJH");
        map.put("化债计划明细",  "DEBT_T_ZWGL_HZJH_DTL");
        map.put("建设项目基本信息",  "DEBT_T_PROJECT_INFO");
        map.put("后续融资需求表", "DEBT_T_PROJECT_RZXQ");
        map.put("项目资产表", "DEBT_T_ZCGL_XMZC");
        map.put("项目资产明细表", "DEBT_T_ZCGL_XMZC_DTL");
        map.put("项目资产汇总表", "DEBT_T_ZCGL_XMZC_HZ");
        map.put("名录信息表", "DEBT_T_RZPT_MLXX");

        List<String> sqls = new LinkedList<>();
        String[] filenames = {"标准版-资产情况表", "标准版-支出事项一户式表", "标准版-债务一户式", "标准版-项目一户式表", "标准版-名录情况"};
        for (String filename : filenames) {
            String filepath = StrUtil.format("D:/Desktop/套表/{}.xlsx", filename);
            System.out.println(filepath);
            // 读取 Excel 文件
            ExcelReader reader = ExcelUtil.getReader(filepath);
            // 存储sheet页名称和数据的Map
            reader.getSheetNames().forEach( sheetName -> {
                // 获取当前 sheet 页的数据
                reader.setSheet(sheetName);
                // reader.read().forEach(row -> {});
                List<List<Object>> read = reader.read();
                List<Object> col_zhs = read.get(0);
                List<Object> cols = read.get(1);
                System.out.println(StrUtil.format("----------- {} --------------", sheetName));
                String tab_name = map.get(sheetName);
                for (int i = 0; i < cols.size(); i++) {
                // sqls.add(StrUtil.format("insert into ai_test(TAB_NAME, RN, COL_NAME, COL_NAME_ZH, IS_SHOW) VALUES ({}, {}, {}, {}, 1)", tab_name, i+1, cols.get(i), col_zhs.get(i)));
                    try {
                        Db.use().execute("insert into ai_test(TAB_NAME, RN, COL_NAME, COL_NAME_ZH, IS_SHOW) VALUES (?, ?, ?, ?, 1)", tab_name, i+1, cols.get(i), col_zhs.get(i));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        }




    }
}
