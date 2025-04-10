package com.aihb.apcahepoi;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class MyGetTableExamInfo {

    @Test
    public void MyTest() {
        File directory = new File("D:\\Program Files\\JetBrains\\project\\Heart\\utils\\hutool\\src\\main\\resources\\files\\jcpt"); // 替换为你的目录路径
        List<File> files = FileUtil.loopFiles(directory);
        for (File file : files) {
//            System.out.println(file.getName());
            if (!file.getName().startsWith("~")) {
                MyProcess(file);
            }
        }
    }

    private static void MyProcess(File file) {
        Map<String, String> hashMap = new HashMap<>();
//        hashMap.put("债务基本信息表", "DEBT_T_ZWGL_ZWXX");
//        hashMap.put("债务举借信息",  "DEBT_T_ZWGL_JJXX");
//        hashMap.put("债务还本计划",  "DEBT_T_ZWGL_HKJH");
//        hashMap.put("债务还本计划（变更）",  "DEBT_T_ZWGL_HKJH_BG");
//        hashMap.put("债务偿还本金",  "DEBT_T_ZWGL_CHBJ");
//        hashMap.put("债务转化表", "DEBT_T_ZWGL_ZWZH");
//        hashMap.put("债务利息罚息",  "DEBT_T_ZWGL_LXFX");
//        hashMap.put("债务支出信息",  "DEBT_T_ZWGL_ZCXX");
//        hashMap.put("债务转贷表", "DEBT_T_ZWGL_ZWZD");
//        hashMap.put("存量债务余额（跑批）",  "DEBT_T_ZWGL_TOTALYE");
//        hashMap.put("FACT债券债务余额（跑批）",  "DEBT_T_FACT_ZQZWYE");
//        hashMap.put("政府支出事项主表",  "DEBT_T_RZPT_ZCSX_SXXX");
//        hashMap.put("政府支出事项动态变化情况表", "DEBT_T_RZPT_ZCSX_XXBG");
//        hashMap.put("政府支出事项纳入预算表", "DEBT_T_RZPT_ZCSX_NRYS");
//        hashMap.put("政府支出事项实际支出表", "DEBT_T_RZPT_ZCSX_SJZC");
//        hashMap.put("支出事项和项目关联表",  "DEBT_T_ZWGL_XMZC_RELATION");
//        hashMap.put("支出事项余额（跑批）",  "DEBT_T_ZCSX_TOTALYE");
//        hashMap.put("隐性债务totalye不考虑重大审批结果的（跑批）", "DEBT_T_YXZW_TOTALYE_V2");
//        hashMap.put("隐性债务totalye考虑重大审批结果的（跑批）",  "DEBT_T_YXZW_TOTALYE_SIMPLE");
//        hashMap.put("隐性债务tochbj不考虑重大审批结果的（跑批）",  "DEBT_T_YXZW_TOCHBJ_V2");
//        hashMap.put("债务认定结果表", "DEBT_T_RZPT_ZWRD");
//        hashMap.put("变动台账表", "DEBT_T_YXZW_BDTZ");
//        hashMap.put("化债计划主单",  "DEBT_T_ZWGL_HZJH");
//        hashMap.put("化债计划明细",  "DEBT_T_ZWGL_HZJH_DTL");
//        hashMap.put("建设项目基本信息",  "DEBT_T_PROJECT_INFO");
//        hashMap.put("后续融资需求表", "DEBT_T_PROJECT_RZXQ");
//        hashMap.put("项目资产表", "DEBT_T_ZCGL_XMZC");
//        hashMap.put("项目资产明细表", "DEBT_T_ZCGL_XMZC_DTL");
//        hashMap.put("项目资产汇总表", "DEBT_T_ZCGL_XMZC_HZ");
//        hashMap.put("名录信息表", "DEBT_T_RZPT_MLXX");
        hashMap.put("名录信息表", "DEBT_T_RZPT_MLXX");
        hashMap.put("建设项目基本信息", "DEBT_T_PROJECT_INFO");
        hashMap.put("后续融资需求表", "DEBT_T_PROJECT_RZXQ");
        hashMap.put("项目资产表", "DEBT_T_ZCGL_XMZC");
        hashMap.put("项目资产明细表", "DEBT_T_ZCGL_XMZC_DTL");
        hashMap.put("项目资产汇总表", "DEBT_T_ZCGL_XMZC_HZ");
        hashMap.put("债务基本信息表", "DEBT_T_ZWGL_ZWXX");
        hashMap.put("债务举借信息", "DEBT_T_ZWGL_JJXX");
        hashMap.put("债务还本计划", "DEBT_T_ZWGL_HKJH");
        hashMap.put("债务还本计划（变更）", "DEBT_T_ZWGL_HKJH_BG");
        hashMap.put("债务偿还本金", "DEBT_T_ZWGL_CHBJ");
        hashMap.put("债务转化表", "DEBT_T_ZWGL_ZWZH");
        hashMap.put("债务利息罚息", "DEBT_T_ZWGL_LXFX");
        hashMap.put("债务支出信息", "DEBT_T_ZWGL_ZCXX");
        hashMap.put("债务转贷表", "DEBT_T_ZWGL_ZWZD");
        hashMap.put("存量债务余额", "DEBT_T_ZWGL_TOTALYE");
        hashMap.put("存量债务利息", "DEBT_T_ZWGL_TOTALLX");
        hashMap.put("债券债务余额", "DEBT_T_FACT_ZQZWYE");
        hashMap.put("政府支出事项主表", "DEBT_T_RZPT_ZCSX_SXXX");
        hashMap.put("政府支出事项动态变化情况表", "DEBT_T_RZPT_ZCSX_XXBG");
        hashMap.put("政府支出事项纳入预算表", "DEBT_T_RZPT_ZCSX_NRYS");
        hashMap.put("政府支出事项实际支出表", "DEBT_T_RZPT_ZCSX_SJZC");
        hashMap.put("支出事项和项目关联表", "DEBT_T_ZWGL_XMZC_RELATION");
        hashMap.put("支出事项余额", "DEBT_T_ZCSX_TOTALYE");
        hashMap.put("隐性债务余额明细表（不考虑党委政府审批）", "DEBT_T_YXZW_TOTALYE_V2");
        hashMap.put("隐性债务余额明细表（考虑党委政府审批）", "DEBT_T_YXZW_TOTALYE_SIMPLE");
        hashMap.put("隐性债务偿还本金明细表", "DEBT_T_YXZW_TOCHBJ_V2");
        hashMap.put("债务认定结果表", "DEBT_T_RZPT_ZWRD");
        hashMap.put("变动台账表", "DEBT_T_YXZW_BDTZ");
        hashMap.put("化债计划主单", "DEBT_T_ZWGL_HZJH");
        hashMap.put("化债计划明细", "DEBT_T_ZWGL_HZJH_DTL");
        hashMap.put("单位信息表", "FASP_T_PUBAGENCY");
        hashMap.put("汇率表", "DEBT_T_EXRATE");
        hashMap.put("项目投资计划", "DEBT_T_ZQGL_XM_PLAN");
        hashMap.put("项目收益信息", "DEBT_T_ZQGL_XM_INCOME");
        hashMap.put("企业财务指标", "DEBT_T_RZPT_CWZB");
        hashMap.put("企业财务指标明细", "DEBT_T_RZPT_CWZB_MX");
        hashMap.put("债务支付费用", "DEBT_T_ZWGL_ZFFY");
        hashMap.put("担保信息表", "DEBT_T_ZWGL_DBXX");
        hashMap.put("对外担保表", "DEBT_T_ZWGL_DWDB");
        hashMap.put("抵押质押表", "DEBT_T_ZWGL_DYZT");
        hashMap.put("支出事项约定支出", "DEBT_T_RZPT_ZCSX_YDZC");
        hashMap.put("政府支出事项社会投资表", "DEBT_T_RZPT_ZCSX_SHTZ");
        hashMap.put("债务化解导入（化债出库信息）", "DEBT_T_RZPT_ZWCK");

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {
            ArrayList<List<Object>> results_list = new ArrayList<>();

            for (int sheetNo = 0; sheetNo < workbook.getNumberOfSheets(); sheetNo++) {
                int is_show = 1;
                Sheet sheet = workbook.getSheetAt(sheetNo);
                String sheetName = workbook.getSheetName(sheetNo);
                // System.out.println("====================" + sheetName + "=======================");
                String tableName = hashMap.getOrDefault(sheetName, "");
                FormulaEvaluator formulaEvaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
                // 获取总行数和总列数
                int totalRows = sheet.getLastRowNum() + 1;
                // System.out.println("总行数：" + totalRows);

                if (true/*totalRows == 2*/) {
                    Row row1 = sheet.getRow(0);
                    Row row2 = sheet.getRow(1);
                    int max = Math.max(row1.getLastCellNum(), row2.getLastCellNum());  // 两行中最大列号
                    for (int colIndex = 0; colIndex < max; colIndex++) {
                        Cell cell1 = row1.getCell(colIndex);
                        if (null == cell1) continue;
                        String col_name_zh = MyGetMergedRegionInfo.getCellValue(formulaEvaluator, cell1);
                        Cell cell2 = row2.getCell(colIndex);
//                        if (null == col_name_zh) System.out.println(file.getName() + "===" + sheetName + "====" + colIndex);
                        if (col_name_zh.equals("黄色背景为隐藏列")) {
                            is_show = 0;
                            continue;
                        }
                        String col_name = MyGetMergedRegionInfo.getCellValue(formulaEvaluator, cell2);
                        results_list.add(Arrays.asList(sheetName, tableName, col_name_zh.trim(), col_name, colIndex+1, is_show));
                        // System.out.println(Arrays.asList(sheetName, tableName, col_name_zh.trim(), col_name, colIndex+1, is_show));
                    }
                }

            }

            // results_list.forEach(System.out::println);
            results_list.forEach(row -> {
                System.out.println(StrUtil.format("insert into test240628(tab_name_zh, tab_name, col_name_zh, col_name, rn, is_show) values ('{}', '{}', '{}', '{}', {}, {});", row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5)));
                /*try {
                    Db.use().execute("insert into ai_test_1(tab_name_zh, tab_name, col_name_zh, col_name, rn, is_show) values (?, ?, ?, ?, ?, ?)", row.get(0), row.get(1), row.get(2), row.get(3), row.get(4), row.get(5)) ;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }*/
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
