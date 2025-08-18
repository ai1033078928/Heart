package com.heart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@ResponseBody
@RequestMapping(value = "/comm", produces = {"application/json;charset=UTF-8"})
@Api(value = "CommonController", tags = "CommonController")
public class CommonController {
    @Value("${app.name}")
    private String appName;

    @ApiOperation("测试")
    @GetMapping("/test")
    public Map test() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", "hello world!!!");
        log.info(StringUtils.join(appName, "test function"));
        return result;
    }

}
