package cn.example.druid.util;

import cn.example.druid.datasource.DataSourceManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作工具类
 */
public class JdbcUtil {

    private static final DataSourceManager dataSourceManager = DataSourceManager.getInstance();

    /**
     * 执行查询操作（从主库）
     */
    public static List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        return executeQuery(DataSourceManager.DataSourceType.MASTER.getName(), sql, params);
    }

    /**
     * 执行查询操作（可指定数据源）
     */
    public static List<Map<String, Object>> executeQuery(String dataSourceName, String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dataSourceManager.getConnection(dataSourceName);
            pstmt = conn.prepareStatement(sql);

            if (null != params) {
                // 设置参数
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }

            rs = pstmt.executeQuery();
            return resultSetToList(rs);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    /**
     * 执行更新操作（插入、更新、删除）- 主库
     */
    public static int executeUpdate(String sql, Object... params) throws SQLException {
        return executeUpdate(DataSourceManager.DataSourceType.MASTER.getName(), sql, params);
    }

    /**
     * 执行更新操作（可指定数据源）
     */
    public static int executeUpdate(String dataSourceName, String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSourceManager.getConnection(dataSourceName);
            pstmt = conn.prepareStatement(sql);

            // 设置参数
            if (null != params) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }

            return pstmt.executeUpdate();
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    /**
     * 执行批量更新操作
     */
    public static int[] executeBatch(String sql, List<Object[]> batchParams) throws SQLException {
        return executeBatch(DataSourceManager.DataSourceType.MASTER.getName(), sql, batchParams);
    }

    /**
     * 执行批量更新操作（可指定数据源）
     */
    public static int[] executeBatch(String dataSourceName, String sql, List<Object[]> batchParams) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dataSourceManager.getConnection(dataSourceName);
            conn.setAutoCommit(false); // 开启事务

            pstmt = conn.prepareStatement(sql);

            // 添加批量参数
            for (Object[] params : batchParams) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
                pstmt.addBatch();
            }

            int[] result = pstmt.executeBatch();
            conn.commit(); // 提交事务
            return result;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // 回滚事务
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // 恢复自动提交
            }
            closeResources(null, pstmt, conn);
        }
    }

    /**
     * 将ResultSet转换为List<Map>
     */
    private static List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                Object value = rs.getObject(i);
                row.put(columnName, value);
            }
            list.add(row);
        }

        return list;
    }

    /**
     * 关闭数据库资源
     */
    private static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // 注意：这里不关闭Connection，因为使用了连接池
    }
}
