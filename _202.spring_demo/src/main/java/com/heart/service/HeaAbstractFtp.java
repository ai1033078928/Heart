package com.heart.service;

import com.heart.execption.FtpException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 */
public abstract class HeaAbstractFtp {

    /**
     * 打开指定目录
     *
     * @param directory directory
     * @return 是否打开目录
     */
    public abstract boolean cd(String directory) throws FtpException;

    /**
     * 打开上级目录
     *
     * @return 是否打开目录
     * @since 4.0.5
     */
    public boolean toParent() throws FtpException {
        return cd("..");
    }

    /**
     * 远程当前目录（工作目录）
     *
     * @return 远程当前目录
     */
    public abstract String pwd() throws FtpException;

    /**
     * 在当前远程目录（工作目录）下创建新的目录
     *
     * @param dir 目录名
     * @return 是否创建成功
     */
    public abstract boolean mkdir(String dir) throws FtpException;

    /**
     * 文件或目录是否存在
     *
     * @param path 目录
     * @return 是否存在
     */
    public boolean exist(String path) throws FtpException {
        final String fileName = FilenameUtils.getName(path);
        final String dir = FilenameUtils.getFullPathNoEndSeparator(path);
        final List<String> names = ls(dir);
        return containsIgnoreCase(names, fileName);
    }


    /**
     * 遍历某个目录下所有文件和目录，不会递归遍历
     *
     * @param path 需要遍历的目录
     * @return 文件和目录列表
     */
    public abstract List<String> ls(String path) throws FtpException;

    /**
     * 删除指定目录下的指定文件
     *
     * @param path 目录路径
     * @return 是否存在
     */
    public abstract boolean delFile(String path) throws FtpException;

    /**
     * 删除文件夹及其文件夹下的所有文件
     *
     * @param dirPath 文件夹路径
     * @return boolean 是否删除成功
     */
    public abstract boolean delDir(String dirPath) throws FtpException;

    /**
     * 创建指定文件夹及其父目录，从根目录开始创建，创建完成后回到默认的工作目录
     *
     * @param dir 文件夹路径，绝对路径
     */
    public void mkDirs(String dir) throws FtpException {
        final String[] dirs = StringUtils.trim(dir).split("[\\\\/]+");

        final String now = pwd();
        if (dirs.length > 0 && StringUtils.isEmpty(dirs[0])) {
            //首位为空，表示以/开头
            this.cd("/");
        }
        for (int i = 0; i < dirs.length; i++) {
            if (StringUtils.isNotEmpty(dirs[i])) {
                if (false == cd(dirs[i])) {
                    //目录不存在时创建
                    mkdir(dirs[i]);
                    cd(dirs[i]);
                }
            }
        }
        // 切换回工作目录
        cd(now);
    }

    /**
     * 将本地文件上传到目标服务器，目标文件名为destPath，若destPath为目录，则目标文件名将与srcFilePath文件名相同。覆盖模式
     *
     * @param srcFilePath 本地文件路径
     * @param destFile    目标文件
     * @return 是否成功
     */
    public abstract boolean upload(String srcFilePath, File destFile) throws FtpException;

    /**
     * 下载文件
     *
     * @param path    文件路径
     * @param outFile 输出文件或目录
     */
    public abstract void download(String path, File outFile) throws FtpException;

    // ---------------------------------------------------------------------------------------------------------------------------------------- Private method start

    /**
     * 是否包含指定字符串，忽略大小写
     *
     * @param names      文件或目录名列表
     * @param nameToFind 要查找的文件或目录名
     * @return 是否包含
     */
    private static boolean containsIgnoreCase(List<String> names, String nameToFind) {
        if (CollectionUtils.isEmpty(names)) {
            return false;
        }
        if (StringUtils.isEmpty(nameToFind)) {
            return false;
        }
        return names.stream().anyMatch(name -> StringUtils.equalsIgnoreCase(name, nameToFind));
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------- Private method end

    /**
     * 关闭连接
     *
     */
    public abstract void close() throws Exception;
}