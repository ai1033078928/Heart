package com.heart.service;

import com.heart.entity.OdsLoadingJobTest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 推送下游任务加载新表 服务类
 * </p>
 *
 * @author ahb
 * @since 2025-11-23
 */
public interface IOdsLoadingJobTestService extends IService<OdsLoadingJobTest> {

    List<OdsLoadingJobTest> getActiveLoadingJobs(List<Integer> timeIntervals);

}
