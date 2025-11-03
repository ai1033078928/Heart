package com.data.utils;

import org.apache.commons.net.ftp.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class FTPUtil implements AutoCloseable {

    private FTPClient ftpClient;
    private String server;
    private int port;
    private String username;
    private String password;

    public void init(String server, int port, String username, String password) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.password = password;
        this.ftpClient = new FTPClient();
    }

    public FTPUtil() {
    }

    /**
     * 连接到FTP服务器
     */
    public boolean connect() throws IOException {
        try {
            ftpClient.connect(server, port);
            ftpClient.login(username, password);
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            return FTPReply.isPositiveCompletion(ftpClient.getReplyCode());
        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    /**
     * 断开FTP连接
     */
    public void disconnect() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * 列出指定目录下的文件名列表
     */
    public List<String> listFileNames(String directory) throws IOException {
        List<String> fileNames = new ArrayList<>();
        FTPFile[] ftpFiles = ftpClient.listFiles(directory);
        if (ftpFiles != null) {
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.isFile()) {
                    fileNames.add(ftpFile.getName());
                }
            }
        }
        return fileNames;
    }

    /**
     * 列出指定目录下的文件详细信息
     */
    public List<FTPFile> listFiles(String directory) throws IOException {
        return Arrays.asList(ftpClient.listFiles(directory));
    }

    /**
     * 获取文件详细信息
     */
    public FTPFile getFile(String remoteFilePath) throws IOException {
        FTPFile[] files = ftpClient.listFiles(remoteFilePath);
        if (files.length > 0) {
            return files[0];
        }
        return null;
    }

    /**
     * 下载文件
     */
    public boolean downloadFile(String remoteFilePath, String localFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
            return ftpClient.retrieveFile(remoteFilePath, fos);
        }
    }

    /**
     * 下载文件到输出流
     */
    public boolean downloadFile(String remoteFilePath, OutputStream outputStream) throws IOException {
        return ftpClient.retrieveFile(remoteFilePath, outputStream);
    }

    /**
     * 上传文件
     */
    public boolean uploadFile(String localFilePath, String remoteFilePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(localFilePath)) {
            return ftpClient.storeFile(remoteFilePath, fis);
        }
    }

    /**
     * 上传文件从输入流
     */
    public boolean uploadFile(InputStream inputStream, String remoteFilePath) throws IOException {
        return ftpClient.storeFile(remoteFilePath, inputStream);
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String remoteFilePath) throws IOException {
        return ftpClient.deleteFile(remoteFilePath);
    }

    /**
     * 创建目录
     */
    public boolean makeDirectory(String directory) throws IOException {
        return ftpClient.makeDirectory(directory);
    }

    /**
     * 删除目录
     */
    public boolean removeDirectory(String directory) throws IOException {
        return ftpClient.removeDirectory(directory);
    }

    /**
     * 切换工作目录
     */
    public boolean changeWorkingDirectory(String directory) throws IOException {
        return ftpClient.changeWorkingDirectory(directory);
    }

    /**
     * 获取当前工作目录
     */
    public String printWorkingDirectory() throws IOException {
        return ftpClient.printWorkingDirectory();
    }

    /**
     * 重命名文件
     */
    public boolean rename(String oldName, String newName) throws IOException {
        return ftpClient.rename(oldName, newName);
    }

    /**
     * 检查文件是否存在
     */
    public boolean exists(String remoteFilePath) throws IOException {
        FTPFile[] files = ftpClient.listFiles(remoteFilePath);
        return files.length > 0;
    }

    /**
     * 获取文件大小
     */
    public long getFileSize(String remoteFilePath) throws IOException {
        FTPFile[] files = ftpClient.listFiles(remoteFilePath);
        if (files.length > 0) {
            return files[0].getSize();
        }
        return -1;
    }

    @Override
    public void close() throws Exception {
        this.disconnect();
    }
}
