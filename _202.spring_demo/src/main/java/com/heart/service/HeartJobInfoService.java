package com.heart.service;

import com.heart.entity.HeartJobInfoEntity;

import java.util.List;

public interface HeartJobInfoService {

    List<HeartJobInfoEntity> getAllJobInfo();

    List<HeartJobInfoEntity> getJobInfoById(List<Long> ids);

    List<HeartJobInfoEntity> getJobInfoByServerName(List<String> serverNames);

    List<HeartJobInfoEntity> getAllJobInfoFromSecondary();

    List<HeartJobInfoEntity> getJobInfoByIdFromSecondary(List<Long> ids);
}
