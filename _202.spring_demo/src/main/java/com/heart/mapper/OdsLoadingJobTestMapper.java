package com.heart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heart.entity.OdsLoadingJobTest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 推送下游任务加载新表 Mapper 接口
 * </p>
 *
 * @author ahb
 * @since 2025-11-23
 */
@Mapper
public interface OdsLoadingJobTestMapper extends BaseMapper<OdsLoadingJobTest> {
    /**
     * 查询激活状态的加载作业，根据时间间隔列表过滤
     * @param timeIntervals 时间间隔列表
     * @return 加载作业列表
     */
    @Select({
            "<script>",
            "SELECT ods_table, time_interval, time_unit, need_cols, from_system, pk_cols,",
            "start_load_time, DATE_ADD(start_load_time, INTERVAL -5 MINUTE) AS adjusted_start_load_time,",
            "last_load_time, delay_time, send_systems, model_table, model_system, load_type",
            "FROM swap.ods_loading_job_test",
            "WHERE load_status = 'Active'",
            "AND load_enable = '1'",
            "AND time_interval IN",
            "<foreach collection='timeIntervals' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<OdsLoadingJobTest> selectActiveLoadingJobs(@Param("timeIntervals") List<Integer> timeIntervals);
}
