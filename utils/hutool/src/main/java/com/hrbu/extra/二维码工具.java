package com.hrbu.extra;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import org.junit.Test;

public class 二维码工具 {
    /**
     * 生成二维码
     *
     * 也可以自行设置 1.样式（颜色，边距） 2.logo小图标 3.调整纠错级别
     */
    @Test
    public void Test() {
        // 生成指定url对应的二维码到文件(也可以到流)，宽和高都是300像素
        QrCodeUtil.generate("http://aihb.top/", 300, 300, FileUtil.file("D:\\Program Files\\JetBrains\\project\\utils\\a_common\\qrcode.jpg"));

    }

    /**
     * 识别二维码
     */
    @Test
    public void Test2() {
        String decode = QrCodeUtil.decode(FileUtil.file("D:\\Program Files\\JetBrains\\project\\Utils\\a_common\\qrcode.jpg"));
        System.out.println(decode);
    }
}
