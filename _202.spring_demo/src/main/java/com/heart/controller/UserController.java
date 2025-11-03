package com.heart.controller;


import com.heart.entity.User;
import com.heart.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@ResponseBody
@RequestMapping(value = "/user", produces = {"application/json;charset=UTF-8"})
@Api(value = "user", tags = "用户操作")
public class UserController {

    @Autowired
    UserService userService;

    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    public Map getUserAll() {
        Map<String, Object> result = new HashMap<>();
        List<User> fromPrimary = userService.getAllFromPrimary();
        result.put("database1", fromPrimary);
        List<User> fromSecondary = userService.getAllFromSecondary();
        result.put("database2", fromSecondary);
        return result;
    }

    @ApiOperation("根据传入的ids获取用户数据")
    @GetMapping("/ids")
    public Map getUserByIds(String ids) {
        Map<String, Object> result = new HashMap<>();
        List<Long> idsList = Arrays.stream(ids.split(","))
                .map(x -> Long.valueOf(x.trim()))
                .collect(Collectors.toList());

        List<User> fromSecondary = userService.getUserByIdFromSecondary(idsList);
        result.put("database2", fromSecondary);
        List<User> fromPrimary = userService.getUserByIdFromPrimary(idsList);
        result.put("database1", fromPrimary);
        return result;
    }
}
