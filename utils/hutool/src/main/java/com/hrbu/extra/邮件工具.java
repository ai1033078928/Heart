package com.hrbu.extra;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import org.junit.Test;

/**
 * 配置文件mail.setting
 */
public class 邮件工具 {

    /**
     * 普通文本
     */
    @Test
    public void Test() {
        MailUtil.send("1033078928@qq.com", "测试", "邮件来自Hutool测试", false);
    }

    /**
     * 群发
     */
    @Test
    public void Test2() {
        MailUtil.send(CollUtil.newArrayList("1033078928@qq.com", "1714686225@qq.com"), "测试", "邮件来自Hutool测试", false);
    }

    /**
     * html格式邮件，并增加附件  465端口
     */
    @Test
    public void Test3() {
        MailUtil.send(CollUtil.newArrayList("1033078928@qq.com"), "遮天", "遮天第一章", true, FileUtil.file("D:\\Program Files\\JetBrains\\project\\Utils\\a_common\\zt.txt"));
    }
}
