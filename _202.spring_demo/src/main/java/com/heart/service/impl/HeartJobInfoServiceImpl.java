package com.heart.service.impl;

import com.heart.annotation.DataSourceSwitcher;
import com.heart.datasource.DataSourceType;
import com.heart.entity.HeartJobInfoEntity;
import com.heart.mapper.HeartJobInfoRepository;
import com.heart.service.HeartJobInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeartJobInfoServiceImpl implements HeartJobInfoService {

    @Autowired
    HeartJobInfoRepository heartJobInfoRepository;

    @Override
    public List<HeartJobInfoEntity> getAllJobInfo() {
        return heartJobInfoRepository.findAll();
    }

    @Override
    public List<HeartJobInfoEntity> getJobInfoById(List<Long> ids) {
        return heartJobInfoRepository.findAllById(ids);
    }

    @Override
    public List<HeartJobInfoEntity> getJobInfoByServerName(List<String> serverNames) {
        return heartJobInfoRepository.findByServerNames(serverNames);
    }


    @DataSourceSwitcher(DataSourceType.SECONDARY)
    @Override
    public List<HeartJobInfoEntity> getAllJobInfoFromSecondary() {
        return null;
    }

    @DataSourceSwitcher(DataSourceType.SECONDARY)
    @Override
    public List<HeartJobInfoEntity> getJobInfoByIdFromSecondary(List<Long> ids) {
        return null;
    }
}
