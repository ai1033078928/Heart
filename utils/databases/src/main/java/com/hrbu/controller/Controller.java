package com.hrbu.controller;

import com.hrbu.service.ProcessServiceImpl;
import org.junit.Test;

import javax.annotation.Resource;

public class Controller {

    @Resource
    ProcessServiceImpl processService;


    @Test
    public void Main() {
        processService.getDataByKey(1);
    }
}
