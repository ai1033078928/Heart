package com.hrbu.service.impl;

import com.hrbu.entity.Process;
import com.hrbu.mapper.ProcessMapper;
import com.hrbu.service.ProcessSerivce;

import javax.annotation.Resource;

public class ProcessServiceImpl implements ProcessSerivce {

    @Resource
    ProcessMapper processMapper;

    public Process getDataByKey(Integer key) {
        return processMapper.selectByPrimaryKey(key);
    }
}
