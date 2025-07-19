package taskdemo;

import javafx.concurrent.Task;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.concurrent.*;
import java.util.stream.*;

public class MyMain {

    // 文件复制任务类
    static class FileCopyTask /*extends Task*/ implements Runnable {
        private final Path sourceFile;
        private final Path destDir;
        private final CountDownLatch latch;

        public FileCopyTask(Path sourceFile, Path destDir, CountDownLatch latch) {
            this.sourceFile = sourceFile;
            this.destDir = destDir;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                // 构建目标文件路径
                Path destFile = destDir.resolve(sourceFile.getFileName().toString());
                
                // 复制文件
                Files.copy(sourceFile, destFile, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("已复制: " + sourceFile.getFileName() + " in thread " + Thread.currentThread().getName());
            } catch (IOException e) {
                System.err.println("复制文件失败: " + sourceFile.getFileName() + " 异常: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        }
    }
    
    // 任务生产者
    static class TaskProducer implements Runnable {
        private final BlockingQueue<Runnable> queue;
        private final Path sourceDir;
        
        public TaskProducer(BlockingQueue<Runnable> queue, Path sourceDir) {
            this.queue = queue;
            this.sourceDir = sourceDir;
        }

        @Override
        public void run() {
            try {
                // 遍历源目录中的所有文件
                Files.list(sourceDir).forEach(file -> {
                    if (Files.isRegularFile(file)) {
                        try {
                            // 将文件复制任务添加到队列
                            queue.put(new FileCopyTask(file, sourceDir.resolveSibling(sourceDir.getFileName() + "_backup"), new CountDownLatch(1)));
                            System.out.println("已添加任务: " + file.getFileName());
                            Thread.sleep(500); // 模拟任务产生延迟
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
                
                System.out.println("任务生产完成");
            } catch (IOException e) {
                System.err.println("处理文件时发生错误: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        // 获取用户目录下的工作目录  System.getProperty("user.home")
        Path workDir = Paths.get("").resolve("thread-file-copy");
        
        // 创建源目录和目标目录
        Path sourceDir = workDir.resolve("source");
        Path destDir = workDir.resolve("destination");
        
        try {
            // 如果目录存在先删除
            if (Files.exists(destDir)) {
                Files.walk(destDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try { Files.delete(path); } 
                        catch (IOException e) {}
                    });
            }
            
            // 创建源目录和目标目录
            Files.createDirectories(sourceDir);
            Files.createDirectories(destDir);
            
            // 在源目录中创建一些测试文件
            for (int i = 1; i <= 5; i++) {
                Path testFile = sourceDir.resolve("test-file-" + i + ".txt");
                Files.write(testFile, ("测试内容 " + i).getBytes());
                System.out.println("创建测试文件: " + testFile.getFileName());
            }
            
            // 创建固定大小的线程池
            int poolSize = 3;
            ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
            
            // 创建阻塞队列
            BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
            
            // 提交生产者任务
            executorService.submit(new TaskProducer(queue, sourceDir));
            
            // 创建CountDownLatch
            CountDownLatch latch = new CountDownLatch(poolSize);
            
            // 提交消费者任务
            for (int i = 0; i < poolSize; i++) {
                executorService.submit(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            Runnable task = queue.poll(3, TimeUnit.SECONDS);
                            if (task != null) {
                                task.run();
                            } else {
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            // 等待所有任务完成
            latch.await();
            
            // 关闭线程池
            executorService.shutdown();
            
            // 验证复制结果
            System.out.println("\n验证复制结果:");
            Files.list(destDir).forEach(file -> {
                try {
                    System.out.println("找到复制文件: " + file.getFileName() + ", 大小: " + Files.size(file) + " bytes");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            
            System.out.println("\n所有任务已完成");
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 清理测试目录（注释掉以便查看结果）
            /*try {
                Files.walk(workDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try { Files.delete(path); } 
                        catch (IOException e) {}
                    });
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }
}
