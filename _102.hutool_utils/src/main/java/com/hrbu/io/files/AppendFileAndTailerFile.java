package com.hrbu.io.files;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileAppender;
import cn.hutool.core.io.file.Tailer;
import cn.hutool.core.thread.ThreadUtil;

import java.util.concurrent.Executor;

public class AppendFileAndTailerFile {
    public static void main(String[] args) {

        // 追加数据
        ThreadUtil.execute(() -> {
            String filename = "e:/log.log";
            // if (FileUtil.exist(filename)) FileUtil.del(filename);
            FileAppender appender = new FileAppender(FileUtil.file(filename), 16, true);
            for (int i = 0; i < 10000; i++) {
                appender.append("message" + i);
                ThreadUtil.safeSleep(1000);
            }
            appender.flush();
        });

        // 实时读取文件
        ThreadUtil.execute(() -> {
            String filename = "e:/log.log";
            if (FileUtil.exist(filename)) {
                Tailer tailer = new Tailer(FileUtil.file(filename), Tailer.CONSOLE_HANDLER, 2);
                tailer.start();
            }
        });


    }
}
