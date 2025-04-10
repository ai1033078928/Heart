package com.hrbu.io.setting;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import org.junit.Test;

public class 配置文件读取 {
    /**
     * 重新加载配置和保存配置
     */
    @Test
    public void Test3() {
        Setting setting = new Setting("example.setting", CharsetUtil.CHARSET_UTF_8, true);
        //重新读取配置文件
        // setting.reload();

        //在配置文件变更时自动加载
        setting.autoLoad(true);

        //当通过代码加入新的键值对的时候，调用store会保存到文件，但是会盖原来的文件，并丢失注释
        setting.set("name1", "value");
        setting.store("XXX.setting");

        //获得所有分组名
        setting.getGroups().forEach(System.out::println);

        //将key-value映射为对象，原理是原理是调用对象对应的setXX方法
        // UserVO userVo = new UserVo();
        // setting.toBean(userVo);

        //设定变量名的正则表达式。
        //Setting的变量替换是通过正则查找替换的，如果Setting中的变量名其他冲突，可以改变变量的定义方式
        //整个正则匹配变量名，分组1匹配key的名字
        setting.setVarRegex("\\$\\{(.*?)\\}");
        System.out.println(setting.getByGroup("user", "demo"));
    }

    /**
     * setting文件读取
     */
    @Test
    public void Test2() {
        Setting setting = new Setting("example.setting", CharsetUtil.CHARSET_UTF_8, true);
        // 获取key为name的值
        System.out.println(setting.getStr("name"));
        //完整的带有key、分组和默认值的获得值得方法
        System.out.println(setting.getStr("url", "demo", "默认值"));
        //有时候需要在key对应value不存在的时候（没有这项设置的时候）告知户，故有此方法打印一个debug日志
        setting.getWithLog("name1");
        setting.getByGroupWithLog("name", "demo");
        //获取分组下所有配置键值对，组成新的Setting
        setting.getSetting("demo");
    }



    /**
     * properties文件读取
     */
    @Test
    public void Test() {
        Props props = new Props("my.properties");
        System.out.println(props.get("file"));
        System.out.println(props.get("monster.id"));
        System.out.println(props.getProperty("monster.name"));
        System.out.println(props.getStr("monster.skill"));
    }


}
