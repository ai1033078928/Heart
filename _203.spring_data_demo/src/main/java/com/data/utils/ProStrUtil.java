package com.data.utils;

import java.util.Arrays;
import java.util.List;

public class ProStrUtil {

    /**
     * dir文件名去掉，重推部分
     * @return
     */
    public static String getFileNameNoPushBatch(String fileName) {
        String[] parts = fileName.split(".");
        return getStrByArr(parts, 6);
    }

    /**
     * 将字符串数据组重新拼接为字符串，去掉某几部分
     * @return
     */
    public static String getStrByArr(String[] parts, Integer... args) {
        StringBuilder sbStr = new StringBuilder();
        List<Integer> integers = Arrays.asList(args);
        for (int i = 0; i < parts.length; i++) {
            if (integers.contains(i)) continue;
            sbStr.append(parts[i]);
        }
        return sbStr.toString();
    }
}
