package cn.example.druid.config;

import com.alibaba.druid.pool.DruidDataSource;

import java.io.InputStream;
import java.util.Properties;

/**
 * 数据源配置类
 */
public class DataSourceConfig {

    public static final Properties properties = new Properties();

    static {

        // 使用类加载器（路径不加斜杠开头）
        try (InputStream input = DataSourceConfig.class.getClassLoader()
                .getResourceAsStream("example/example.db.properties")) {

            if (input == null) {
                System.out.println("配置文件未找到");
            }

            properties.load(input);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建主数据源
     */
    public static DruidDataSource createMasterDataSource() {
        DruidDataSource dataSource = new DruidDataSource();

        // 基本配置
        dataSource.setUrl(properties.getProperty("jdbc.url"));
        dataSource.setUsername(properties.getProperty("jdbc.username"));
        dataSource.setPassword(properties.getProperty("jdbc.password"));
        dataSource.setDriverClassName(properties.getProperty("jdbc.driver"));

        // 连接池配置
        dataSource.setInitialSize(2);           // 初始连接数
        dataSource.setMinIdle(2);               // 最小空闲连接数
        dataSource.setMaxActive(20);            // 最大连接数
        dataSource.setMaxWait(60000);           // 获取连接等待超时时间
        dataSource.setTimeBetweenEvictionRunsMillis(60000); // 配置间隔多久才进行一次检测
        dataSource.setMinEvictableIdleTimeMillis(300000);   // 配置一个连接在池中最小生存的时间
        dataSource.setValidationQuery("SELECT 1");          // 检测连接是否有效的sql
        dataSource.setTestWhileIdle(true);                  // 建议配置为true，不影响性能，并且保证安全性
        dataSource.setTestOnBorrow(false);                  // 申请连接时执行validationQuery检测连接是否有效
        dataSource.setTestOnReturn(false);                  // 归还连接时执行validationQuery检测连接是否有效

        // 配置监控统计拦截的filters
        try {
            dataSource.setFilters("stat,wall,log4j");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataSource;
    }

    /**
     * 创建从数据源
     */
    public static DruidDataSource createSlaveDataSource() {
        DruidDataSource dataSource = new DruidDataSource();

        // 基本配置
        dataSource.setUrl(properties.getProperty("jdbc.url.slave"));
        dataSource.setUsername(properties.getProperty("jdbc.username.slave"));
        dataSource.setPassword(properties.getProperty("jdbc.password.slave"));
        dataSource.setDriverClassName(properties.getProperty("jdbc.driver.slave"));

        // 连接池配置
        dataSource.setInitialSize(2);
        dataSource.setMinIdle(2);
        dataSource.setMaxActive(15);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        // 配置监控统计拦截的filters
        try {
            dataSource.setFilters("stat");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataSource;
    }
}
