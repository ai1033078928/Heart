package com.heart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.heart.entity.OdsLoadingJobTest;
import com.heart.mapper.OdsLoadingJobTestMapper;
import com.heart.service.IOdsLoadingJobTestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 推送下游任务加载新表 服务实现类
 * </p>
 *
 * @author ahb
 * @since 2025-11-23
 */
@Service
public class OdsLoadingJobTestServiceImpl extends ServiceImpl<OdsLoadingJobTestMapper, OdsLoadingJobTest> implements IOdsLoadingJobTestService {

    @Resource
    private OdsLoadingJobTestMapper odsLoadingJobTestMapper;

    @Override
    public List<OdsLoadingJobTest> getActiveLoadingJobs(List<Integer> timeIntervals) {
        return odsLoadingJobTestMapper.selectActiveLoadingJobs(timeIntervals);
    }
}
