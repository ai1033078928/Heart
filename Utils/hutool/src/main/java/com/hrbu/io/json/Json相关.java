package com.hrbu.io.json;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Json相关 {

    private String jsonStr = "{\"appId\":\"\",\"timestamp\":1642926098,\"nonceStr\":\"VLyigbixrv\",\"signature\":\"e57e0fdf59ce34efda440153656b72194d3bd9c5\",\"jsApiList\":[\"updateAppMessageShareData\",\"updateTimelineShareData\"]}";

    /**
     * java对象转为Json
     */
    @Test
    public void Test3()  {
        Student student = new Student("1", "ahb", "男", "24");

        // javaBean转为json (不忽略空值)
        JSONObject jsonObject = JSONUtil.parseObj(student, false);
        System.out.println(JSONUtil.toJsonPrettyStr(jsonObject));

        // jsonObject.setDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置时间格式
    }

    /**
     * XML字符串转换为JSON
     * JSON转换为XML
     *
     * JSON转Bean
     * readXXX ： 从JSON文件中读取JSON对象的快捷方法
     */
    @Test
    public void Test2()  {
        // 从文件中读取
        JSON json = JSONUtil.readJSON(new File("src/main/resources/test.json"), StandardCharsets.UTF_8);
        // System.out.println(JSONUtil.toJsonPrettyStr(json));
        System.out.println(JSONUtil.parseObj(json).getJSONArray("jsApiList"));
    }

    /**
     * 根据字符串创建一个json对象
     */
    @Test
    public void Test() {
        // 字符串解析
        JSONObject json1 = JSONUtil.parseObj(jsonStr);
        System.out.println(json1.getStr("timestamp"));
        System.out.println(json1.getInt("timestamp"));
        System.out.println(json1.getLong("timestamp"));
        System.out.println(json1.getDouble("timestamp"));
        System.out.println(json1.getBigDecimal("timestamp"));

        // 格式化json(格式化JSON字符串，此方法并不严格检查JSON的格式正确与否)
        System.out.println(JSONUtil.formatJsonStr(jsonStr));

        // 转换为格式化后的JSON字符串
        System.out.println(JSONUtil.toJsonPrettyStr(jsonStr));
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Student{
    private String id;
    private String name;
    private String sex;
    private String age;
}