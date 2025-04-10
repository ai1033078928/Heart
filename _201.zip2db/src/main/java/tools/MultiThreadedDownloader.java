package tools;

import java.io.*;
import java.net.*;

public class MultiThreadedDownloader implements Runnable {

    private String url;  // 下载链接
    private String fileName; // 保存文件名
    private int startByte; // 开始字节
    private int endByte; // 结束字节

    public MultiThreadedDownloader(String url, String fileName, int startByte, int endByte) {
        this.url = url;
        this.fileName = fileName;
        this.startByte = startByte;
        this.endByte = endByte;
    }

    @Override
    public void run() {
        try {
            URL downloadUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) downloadUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
            InputStream inputStream = conn.getInputStream();
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            raf.seek(startByte);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                raf.write(buffer, 0, length);
            }
            raf.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        String url = "https://cdn.aliyundrive.net/downloads/apps/desktop/aDrive-4.9.14.exe?spm=aliyundrive.index.0.0.1b026f60Un4QX0&file=aDrive-4.9.14.exe"; // 下载链接
        String fileName = "file.zip"; // 保存文件名
        URL downloadUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) downloadUrl.openConnection();
        int fileSize = conn.getContentLength();
        int threadNum = 4; // 线程数
//        int fileSize = 1024 * 1024 * 100; // 文件总大小
        int blockSize = fileSize / threadNum; // 每个线程下载的块大小
        for (int i = 0; i < threadNum; i++) {
            int startByte = i * blockSize;
            int endByte = (i + 1) * blockSize - 1;
            if (i == threadNum - 1) {
                endByte = fileSize - 1;
            }
            MultiThreadedDownloader downloader = new MultiThreadedDownloader(url, fileName, startByte, endByte);
            Thread thread = new Thread(downloader);
            thread.start();
        }
    }
}