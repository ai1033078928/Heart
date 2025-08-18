package com.heart.utils;

import com.heart.datasource.DataSourceContextHolder;
import com.heart.datasource.DataSourceType;

import java.util.function.Supplier;

/**
 * 手动数据源切换工具类
 */
public class DataSourceSwitcherUtil {

    public static void usePrimary() {
        DataSourceContextHolder.setDataSourceType(DataSourceType.PRIMARY);
    }

    public static void useSecondary() {
        DataSourceContextHolder.setDataSourceType(DataSourceType.SECONDARY);
    }

    public static void clear() {
        DataSourceContextHolder.clearDataSourceType();
    }

    public static <T> T executeWithPrimary(Supplier<T> supplier) {
        DataSourceContextHolder.setDataSourceType(DataSourceType.PRIMARY);
        try {
            return supplier.get();
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }

    public static <T> T executeWithSecondary(Supplier<T> supplier) {
        DataSourceContextHolder.setDataSourceType(DataSourceType.SECONDARY);
        try {
            return supplier.get();
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }
}
