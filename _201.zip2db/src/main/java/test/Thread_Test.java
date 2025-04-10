package test;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Thread_Test {

    private static final int THREAD_COUNT = 5;  // 假设我们使用5个线程来导出数据


    @Test
    public void thread_Test1() throws InterruptedException, ExecutionException {
        long time1 = System.currentTimeMillis();
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        // 假设数据分成5个部分，使用5个线程并行处理
        List<Future<Void>> futures = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int part = i;  // 每个线程处理不同的数据部分
            futures.add(executorService.submit(() -> {
                exportData(part);
                return null; // 返回类型为Void
            }));
        }

        // 等待所有线程完成任务
        for (Future<Void> future : futures) {
            future.get();
        }

        executorService.shutdown();
        long time2 = System.currentTimeMillis();
        System.out.println(String.valueOf(time2 - time1) + "ms");
    }


    @Test
    public void thread_Test2() {
        long time1 = System.currentTimeMillis();
        for (int part = 0; part < 5; part++) {
            exportData(part);
        }
        long time2 = System.currentTimeMillis();
        System.out.println(String.valueOf(time2 - time1) + "ms");
    }

    // 模拟数据导出任务
    private static void exportData(int part) {
        try {
            // 模拟从数据库查询数据并导出到文件
            String fileName = "files/data_part_" + part + ".txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                // 模拟导出数据
                for (int i = 0; i < 10000000; i++) {
                    writer.write("Data item " + (part * 10000000 + i) + "\n");
                }
            }
            System.out.println("Data part " + part + " exported successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
