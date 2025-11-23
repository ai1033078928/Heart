package com.heart.service.impl;

import com.heart.execption.FtpException;
import com.heart.service.HeaAbstractFtp;
import com.heart.utils.FtpClientPool;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ftp 工具类实现
 *
 * @author liuchao
 * @date 2020/6/28
 */
public class HeaFtp extends HeaAbstractFtp {

    private FTPClient client;
    private FtpClientPool ftpClientPool;

    public HeaFtp(FtpClientPool ftpClientPool, FTPClient client) {
        this.ftpClientPool = ftpClientPool;
        this.client = client;
    }

    public HeaFtp(FtpClientPool ftpClientPool) throws Exception {
        this.ftpClientPool = ftpClientPool;
        this.client = ftpClientPool.borrowObject();
    }

    /**
     * 执行完操作是否返回当前目录
     */
    private boolean backToPwd;

    /**
     * 设置执行完操作是否返回当前目录
     *
     * @param backToPwd 执行完操作是否返回当前目录
     * @return this
     */
    public HeaFtp setBackToPwd(boolean backToPwd) {
        this.backToPwd = backToPwd;
        return this;
    }

    /**
     * 改变目录
     *
     * @param directory 目录
     * @return 是否成功
     */
    @Override
    public boolean cd(String directory) throws FtpException {
        if (StringUtils.isBlank(directory)) {
            return false;
        }

        boolean flag;
        try {
            flag = client.changeWorkingDirectory(directory);
        } catch (IOException e) {
            throw new FtpException(e);
        }
        return flag;
    }

    /**
     * 远程当前目录
     *
     * @return 远程当前目录
     */
    @Override
    public String pwd() throws FtpException {
        try {
            return client.printWorkingDirectory();
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    @Override
    public List<String> ls(String path) throws FtpException {
        final FTPFile[] ftpFiles = lsFiles(path);

        final List<String> fileNames = new ArrayList<>();
        for (FTPFile ftpFile : ftpFiles) {
            fileNames.add(ftpFile.getName());
        }
        return fileNames;
    }

    /**
     * 遍历某个目录下所有文件和目录，不会递归遍历
     *
     * @param path 目录
     * @return 文件或目录列表
     */
    public FTPFile[] lsFiles(String path) throws FtpException {
        String pwd = null;
        if (StringUtils.isNotBlank(path)) {
            pwd = pwd();
            cd(path);
        }

        FTPFile[] ftpFiles;
        try {
            ftpFiles = this.client.listFiles();
        } catch (IOException e) {
            throw new FtpException(e);
        } finally {
            // 回到原目录
            cd(pwd);
        }

        return ftpFiles;
    }

    @Override
    public boolean mkdir(String dir) throws FtpException {
        boolean flag = true;
        try {
            flag = this.client.makeDirectory(dir);
        } catch (IOException e) {
            throw new FtpException(e);
        }
        return flag;
    }

    /**
     * 判断ftp服务器文件是否存在
     *
     * @param path 文件路径
     * @return 是否存在
     */
    public boolean existFile(String path) throws FtpException {
        FTPFile[] ftpFileArr;
        try {
            ftpFileArr = client.listFiles(path);
        } catch (IOException e) {
            throw new FtpException(e);
        }
        if (ArrayUtils.isNotEmpty(ftpFileArr)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean delFile(String path) throws FtpException {
        final String pwd = pwd();
        final String fileName = FilenameUtils.getName(path);
        final String dir = FilenameUtils.getFullPathNoEndSeparator(path);
        cd(dir);
        boolean isSuccess;
        try {
            isSuccess = client.deleteFile(fileName);
        } catch (IOException e) {
            throw new FtpException(e);
        } finally {
            // 回到原目录
            cd(pwd);
        }
        return isSuccess;
    }

    @Override
    public boolean delDir(String dirPath) throws FtpException {
        FTPFile[] dirs;
        try {
            dirs = client.listFiles(dirPath);
        } catch (IOException e) {
            throw new FtpException(e);
        }
        String name;
        String childPath;
        for (FTPFile ftpFile : dirs) {
            name = ftpFile.getName();
            childPath = String.format("%s/%s", dirPath, name);
            if (ftpFile.isDirectory()) {
                // 上级和本级目录除外
                if (false == name.equals(".") && false == name.equals("..")) {
                    delDir(childPath);
                }
            } else {
                delFile(childPath);
            }
        }

        // 删除空目录
        try {
            return this.client.removeDirectory(dirPath);
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 上传文件到指定目录，可选：
     *
     * <pre>
     * 1. path为null或""上传到当前路径
     * 2. path为相对路径则相对于当前路径的子路径
     * 3. path为绝对路径则上传到此路径
     * </pre>
     *
     * @param path 服务端路径，可以为{@code null} 或者相对路径或绝对路径
     * @param file 文件
     * @return 是否上传成功
     */
    @Override
    public boolean upload(String path, File file) throws FtpException {
        Validate.notNull(file, "file to upload is null !");
        return upload(path, file.getName(), file);
    }

    /**
     * 上传文件到指定目录，可选：
     *
     * <pre>
     * 1. path为null或""上传到当前路径
     * 2. path为相对路径则相对于当前路径的子路径
     * 3. path为绝对路径则上传到此路径
     * </pre>
     *
     * @param file     文件
     * @param path     服务端路径，可以为{@code null} 或者相对路径或绝对路径
     * @param fileName 自定义在服务端保存的文件名
     * @return 是否上传成功
     */
    public boolean upload(String path, String fileName, File file) throws FtpException {
        try (InputStream in =  new FileInputStream(file)) {
            return upload(path, fileName, in);
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 上传文件到指定目录，可选：
     *
     * <pre>
     * 1. path为null或""上传到当前路径
     * 2. path为相对路径则相对于当前路径的子路径
     * 3. path为绝对路径则上传到此路径
     * </pre>
     *
     * @param path       服务端路径，可以为{@code null} 或者相对路径或绝对路径
     * @param fileName   文件名
     * @param fileStream 文件流
     * @return 是否上传成功
     */
    public boolean upload(String path, String fileName, InputStream fileStream) throws FtpException {
        try {
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
        } catch (IOException e) {
            throw new FtpException(e);
        }

        String pwd = null;
        if (this.backToPwd) {
            pwd = pwd();
        }

        if (StringUtils.isNotBlank(path)) {
            mkDirs(path);
            boolean isOk = cd(path);
            if (false == isOk) {
                return false;
            }
        }

        try {
            return client.storeFile(fileName, fileStream);
        } catch (IOException e) {
            throw new FtpException(e);
        } finally {
            if (this.backToPwd) {
                cd(pwd);
            }
        }
    }

    /**
     * 下载文件
     *
     * @param path    文件路径
     * @param outFile 输出文件或目录
     */
    @Override
    public void download(String path, File outFile) throws FtpException {
        final String fileName = FilenameUtils.getName(path);
        final String dir = FilenameUtils.getFullPathNoEndSeparator(path);
        download(dir, fileName, outFile);
    }

    /**
     * 下载文件
     *
     * @param path     文件路径
     * @param fileName 文件名
     * @param outFile  输出文件或目录
     */
    public void download(String path, String fileName, File outFile) throws FtpException {
        if (outFile.isDirectory()) {
            outFile = new File(outFile, fileName);
        }
        if (false == outFile.exists()) {
            try {
                FileUtils.touch(outFile);
            } catch (IOException e) {
                throw new FtpException(e);
            }
        }
        try (OutputStream out = FileUtils.openOutputStream(outFile)) {
            download(path, fileName, out);
        } catch (IOException e) {
            throw new FtpException(e);
        }
    }

    /**
     * 下载文件到输出流
     *
     * @param path     文件路径
     * @param fileName 文件名
     * @param out      输出位置
     */
    public void download(String path, String fileName, OutputStream out) throws FtpException {
        String pwd = null;
        if (this.backToPwd) {
            pwd = pwd();
        }
        cd(path);
        try {
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            client.retrieveFile(fileName, out);
        } catch (IOException e) {
            throw new FtpException(e);
        } finally {
            if (backToPwd) {
                cd(pwd);
            }
        }
    }

    @Override
    public void close() {
        ftpClientPool.returnObject(client);
    }

    public void setClient(FTPClient client) {
        this.client = client;
    }
}