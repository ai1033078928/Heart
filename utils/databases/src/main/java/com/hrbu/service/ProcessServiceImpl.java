package com.hrbu.service;

import com.hrbu.entity.Process;
import com.hrbu.mapper.ProcessMapper;

import javax.annotation.Resource;

public class ProcessServiceImpl implements ProcessSerivce {

    @Resource
    ProcessMapper processMapper;

    public Process getDataByKey(Integer key) {
        return processMapper.selectByPrimaryKey(key);
    }
}
