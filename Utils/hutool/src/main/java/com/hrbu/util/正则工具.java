package com.hrbu.util;

import cn.hutool.core.util.ReUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class 正则工具 {

    private final String[] strs = {
            "ZZZaaabbbccc中文1234",
            "ZZZaaabbbccc中文1234,:345/"
    };
    /**
     * 正则提取匹配到的数据
     */
    @Test
    public void Test() {
        String str1 = ReUtil.extractMulti("(\\w+).*(\\d+)", strs[0], "$1\t$2");      // 提取出匹配到的内容
        ArrayList<String> list = ReUtil.findAll("([\\u4e00-\\u9fa5]+)(\\d{2})", strs[0], 2, new ArrayList<String>());     //查找所有匹配文本(0所有 1第一个括号内 2第二个括号内)

        Integer firstNumber = ReUtil.getFirstNumber(strs[1]);                 // 找到匹配到的第一段数字
        boolean match = ReUtil.isMatch("\\w{12}.*\\d{4}", strs[0]);     // 正则式是否完全匹配字符串

        String str2 = ReUtil.replaceAll(strs[1], "(\\d+)", "($1)");// 将匹配到的部分替换为指定值

        String str3 = ReUtil.escape("hello ${world}!!");        // 正则关键字转义


        System.out.println(str1);
        list.forEach(v -> System.out.printf(v + " "));
        System.out.println();
        System.out.println(firstNumber);
        System.out.println(match);
        System.out.println(str2);
        System.out.println(str3);
        // Assert.assertEquals("A-z", str);
    }

    /**
     * 删除匹配到的内容
     */
    @Test
    public void Test2() {
        String str1 = ReUtil.delFirst("\\w+", strs[0]);         // 删除第一次匹配到的内容
        String str2 = ReUtil.delFirst("[\\u4e00-\\u9fa5]+", strs[0]);

        String str3 = ReUtil.delAll("[0-9]{1}", strs[0]);       // 删除所有匹配到的内容
        String str4 = ReUtil.delLast("[0-9]{1}", strs[0]);      // 删除最后一次匹配到的内容
        String str5 = ReUtil.delPre("[0-9]{1}", strs[0]);       // 第一次匹配到字符之前的所有内容

        System.out.println(str1 + "\n" + str2 + "\n" + str3 + "\n" + str4 + "\n" + str5 );
        // Assert.assertEquals("A-z", str);
    }
}
