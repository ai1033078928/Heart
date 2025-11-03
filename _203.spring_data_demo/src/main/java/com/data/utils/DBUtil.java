package com.data.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DBUtil {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DBUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 执行查询SQL，返回List<Map<String, Object>>
     */
    public List<Map<String, Object>> queryData(String sql) {
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * 执行带参数的查询SQL，返回List<Map<String, Object>>
     */
    public List<Map<String, Object>> queryData(String sql, Object... args) {
        return jdbcTemplate.queryForList(sql, args);
    }

    /**
     * 执行查询SQL，返回单个Map
     */
    public Map<String, Object> queryForMap(String sql) {
        return jdbcTemplate.queryForMap(sql);
    }

    /**
     * 执行带参数的查询SQL，返回单个Map
     */
    public Map<String, Object> queryForMap(String sql, Object... args) {
        return jdbcTemplate.queryForMap(sql, args);
    }

    /**
     * 执行查询SQL，返回单个值
     */
    public <T> T queryForObject(String sql, Class<T> requiredType) {
        return jdbcTemplate.queryForObject(sql, requiredType);
    }

    /**
     * 执行带参数的查询SQL，返回单个值
     */
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
        return jdbcTemplate.queryForObject(sql, requiredType, args);
    }

    /**
     * 执行查询SQL，使用RowMapper映射结果
     */
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * 执行带参数的查询SQL，使用RowMapper映射结果
     */
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
        return jdbcTemplate.query(sql, rowMapper, args);
    }

    /**
     * 执行更新操作（INSERT/UPDATE/DELETE）
     */
    public int update(String sql) {
        return jdbcTemplate.update(sql);
    }

    /**
     * 执行带参数的更新操作（INSERT/UPDATE/DELETE）
     */
    public int update(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
    }

    /**
     * 批量执行更新操作
     */
    public int[] batchUpdate(String... sqls) {
        return jdbcTemplate.batchUpdate(sqls);
    }

    /**
     * 批量执行相同结构的更新操作
     */
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    /**
     * 执行INSERT操作并返回影响的行数
     */
    public int insert(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
    }

    /**
     * 执行UPDATE操作并返回影响的行数
     */
    public int updateRecord(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
    }

    /**
     * 执行DELETE操作并返回影响的行数
     */
    public int delete(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
    }

    /**
     * 检查是否存在满足条件的记录
     */
    public boolean exists(String sql, Object... args) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM (" + sql + ") AS subquery",
                Integer.class,
                args
            );
            return count != null && count > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    /**
     * 获取记录数量
     */
    public int count(String sql, Object... args) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM (" + sql + ") AS subquery",
                Integer.class,
                args
            );
        } catch (DataAccessException e) {
            return 0;
        }
    }

    /**
     * 执行任意SQL语句
     */
    public void execute(String sql) {
        jdbcTemplate.execute(sql);
    }

    /**
     * 获取JdbcTemplate实例
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
