package com.hrbu.util;

import cn.hutool.core.util.DesensitizedUtil;
import org.junit.Test;

/**
 * 现阶段支持的脱敏数据类型包括：
 *
 * 用户id
 * 中文姓名
 * 身份证号
 * 座机号
 * 手机号
 * 地址
 * 电子邮件
 * 密码
 * 中国大陆车牌，包含普通车辆、新能源车辆
 * 银行卡
 * 整体来说，所谓脱敏就是隐藏掉信息中的一部分关键信息，用*代替，自定义隐藏可以使用StrUtil.hide方法完成。
 */
public class 信息脱敏工具 {

    /**
     * 身份证号脱敏
     */
    @Test
    public void Test() {
        String idCardNum = DesensitizedUtil.idCardNum("51343620000320711X", 1, 2);
        System.out.println(idCardNum);
    }
}
