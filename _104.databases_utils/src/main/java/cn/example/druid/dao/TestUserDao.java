package cn.example.druid.dao;

import cn.example.druid.datasource.DataSourceManager;
import cn.example.druid.util.JdbcUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class TestUserDao {

    DataSourceManager instance;

    @Before // junit 非正常用法
    public void createDataSource() {
        instance = DataSourceManager.getInstance();
    }

    @Test
    public void createTableTest() throws SQLException {
        String createTabSql = "create table if not exists t_user (\n" +
                "name varchar(100),\n" +
                "age int,\n" +
                "sex bool\n" +
                ");";


        String[] dataSourceNames = instance.getDataSourceNames();
        for (String dataSourceName : dataSourceNames) {
            JdbcUtil.executeUpdate(dataSourceName, createTabSql, null);
        }
    }

    @Test
    public void putData() throws SQLException {
        String insertTabSql = "insert into t_user values(?, ?, ?)";


        Random random = new Random();
        LinkedList<Object[]> objects = new LinkedList<>();

        for (int i = 1; i < 1000; i++) {
            int i1 = random.nextInt(100);
            objects.add(new Object[]{"username-" + i, i1 + 1, i1%2});
        }

        String[] dataSourceNames = instance.getDataSourceNames();
        for (String dataSourceName : dataSourceNames) {
            JdbcUtil.executeBatch(dataSourceName, insertTabSql, objects);
        }
    }


    @Test
    public void getDataList() throws SQLException {
        String createTabSql = "select * from t_user limit 100";


        String[] dataSourceNames = instance.getDataSourceNames();
        for (String dataSourceName : dataSourceNames) {
            List<Map<String, Object>> maps = JdbcUtil.executeQuery(dataSourceName, createTabSql, null);

            System.out.println(StringUtils.join("================== ", dataSourceName, " ===================="));
            maps.forEach(System.out::println);
        }
    }

    @After
    public void closeDataSource() {
        instance.closeAllDataSources();
    }
}
