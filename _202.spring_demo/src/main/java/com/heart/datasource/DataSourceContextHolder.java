package com.heart.datasource;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据源持有者
 * 使用 ThreadLocal 存储当前线程的数据源类型，确保线程安全
 */
@Slf4j
public class DataSourceContextHolder {

    /*
     * 当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<DataSourceType> CONTEXT_HOLDER = new ThreadLocal<>();

    /*
     * 管理所有的数据源id;
     * 主要是为了判断数据源是否存在;
     */
    public static List<DataSourceType> dataSourceIds  = new ArrayList<>();

    // 设置数据源
    public static void setDataSourceType(DataSourceType dataSourceType) {
        log.info("切换到{}数据源", dataSourceType);
        CONTEXT_HOLDER.set(dataSourceType);
    }

    // 获取数据源
    public static DataSourceType getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    // 清除数据源
    public static void clearDataSourceType() {
        CONTEXT_HOLDER.remove();
    }

    public static void saveDataSourceTypeName(DataSourceType dataSourceType){
        dataSourceIds.add(dataSourceType);
    }

    /**
     * 判断指定DataSrouce当前是否存在
     */
    public static boolean containsDataSource(DataSourceType dataSourceType){
        return dataSourceIds.contains(dataSourceType);
    }
}
