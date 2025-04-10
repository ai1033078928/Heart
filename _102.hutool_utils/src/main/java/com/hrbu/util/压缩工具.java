package com.hrbu.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.zip.ZipEntry;

public class 压缩工具 {

    final String sourceFile = "D:\\Program Files\\JetBrains\\project\\Utils\\a_common\\zt.txt";
    final String sourceFile2 = "D:\\Program Files\\JetBrains\\project\\Utils\\a_common\\num.csv";
    final String tagFile = "D:\\Program Files\\JetBrains\\project\\Utils\\a_common\\all.zip";
    final String tagFile2 = "D:\\Program Files\\JetBrains\\project\\Utils\\a_common\\all";

    /**
     *  zip压缩
     */
    @Test
    public void Test() {
        // ZipUtil.zip(sourceFile, tagFile);

        File zip = ZipUtil.zip(FileUtil.file(tagFile), false,
                FileUtil.file(sourceFile),
                FileUtil.file(sourceFile2));    // 多文件或目录压缩
        System.out.println(zip);
    }

    /**
     *  zip解压缩
     */
    @Test
    public void Test2() {
        File unzip = ZipUtil.unzip(tagFile, tagFile2);
        System.out.println(unzip);
    }


    /**
     * Gzip是网页传输中广泛使用的压缩方式，Hutool同样提供其工具方法简化其过程。
     * ZipUtil.gzip 压缩，可压缩字符串，也可压缩文件 ZipUtil.unGzip 解压Gzip文件
     *
     *
     * ZipUtil.zlib 压缩，可压缩字符串，也可压缩文件 ZipUtil.unZlib 解压zlib文件
     * 注意 ZipUtil默认情况下使用系统编码，也就是说：
     * 如果你在命令行下运行，则调用系统编码（一般Windows下为GBK、Linux下为UTF-8）
     * 如果你在IDE（如Eclipse）下运行代码，则读取的是当前项目的编码（详细请查阅IDE设置，我的项目默认都是UTF-8编码，因此解压和压缩都是用这个编码）
     */
}
