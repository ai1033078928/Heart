package cn.example.druid.datasource;

import cn.example.druid.config.DataSourceConfig;
import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 多数据源管理器
 */
public class DataSourceManager {

    // 数据源类型枚举
    public enum DataSourceType {
        MASTER("master"),
        SLAVE("slave");

        private final String name;

        DataSourceType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // 单例实例
    private static volatile DataSourceManager instance;

    // 数据源集合
    private final Map<String, DruidDataSource> dataSourceMap;

    // 读写锁
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private DataSourceManager() {
        dataSourceMap = new HashMap<>();
        initializeDataSources();
    }

    /**
     * 获取单例实例
     */
    public static DataSourceManager getInstance() {
        if (instance == null) {
            synchronized (DataSourceManager.class) {
                if (instance == null) {
                    instance = new DataSourceManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化数据源
     */
    private void initializeDataSources() {
        // 创建主数据源
        DruidDataSource masterDataSource = DataSourceConfig.createMasterDataSource();
        dataSourceMap.put(DataSourceType.MASTER.getName(), masterDataSource);

        // 创建从数据源
        DruidDataSource slaveDataSource = DataSourceConfig.createSlaveDataSource();
        dataSourceMap.put(DataSourceType.SLAVE.getName(), slaveDataSource);
    }

    /**
     * 获取指定名称的数据源
     */
    public DruidDataSource getDataSource(String dataSourceName) {
        lock.readLock().lock();
        try {
            return dataSourceMap.get(dataSourceName);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取主数据源
     */
    public DruidDataSource getMasterDataSource() {
        return getDataSource(DataSourceType.MASTER.getName());
    }

    /**
     * 获取从数据源
     */
    public DruidDataSource getSlaveDataSource() {
        return getDataSource(DataSourceType.SLAVE.getName());
    }

    /**
     * 从指定数据源获取连接
     */
    public Connection getConnection(String dataSourceName) throws SQLException {
        DruidDataSource dataSource = getDataSource(dataSourceName);
        if (dataSource == null) {
            throw new SQLException("数据源 [" + dataSourceName + "] 不存在");
        }
        return dataSource.getConnection();
    }

    /**
     * 从主数据源获取连接
     */
    public Connection getMasterConnection() throws SQLException {
        return getConnection(DataSourceType.MASTER.getName());
    }

    /**
     * 从从数据源获取连接
     */
    public Connection getSlaveConnection() throws SQLException {
        return getConnection(DataSourceType.SLAVE.getName());
    }

    /**
     * 添加新的数据源
     */
    public void addDataSource(String name, DruidDataSource dataSource) {
        lock.writeLock().lock();
        try {
            dataSourceMap.put(name, dataSource);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 移除数据源
     */
    public void removeDataSource(String name) {
        lock.writeLock().lock();
        try {
            DruidDataSource dataSource = dataSourceMap.remove(name);
            if (dataSource != null) {
                dataSource.close();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 关闭所有数据源
     */
    public void closeAllDataSources() {
        lock.writeLock().lock();
        try {
            for (DruidDataSource dataSource : dataSourceMap.values()) {
                if (dataSource != null) {
                    dataSource.close();
                }
            }
            dataSourceMap.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 获取所有数据源名称
     */
    public String[] getDataSourceNames() {
        lock.readLock().lock();
        try {
            return dataSourceMap.keySet().toArray(new String[0]);
        } finally {
            lock.readLock().unlock();
        }
    }
}
